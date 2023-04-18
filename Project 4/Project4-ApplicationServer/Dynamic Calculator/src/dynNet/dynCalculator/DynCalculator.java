package dynNet.dynCalculator;

import java.io.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import utils.PropertyHandler;
import dynNet.httpClassLoader.HTTPClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class [DynCalculator]
 * 
 * Implements a generic calculator, which has no functionality of its own.
 * All operations requested result in loading the corresponding classes from the network dynamically.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 */
public class DynCalculator {

    // Classloader that loads required operations' classes
    private HTTPClassLoader operationsLoader = null;
    
    // Operations cache
    private HashMap <String, Operation> operationsCache = new HashMap<>();

    // Properties handler
    private PropertyHandler configuration = null;

    // view parts
    private final JFrame applicationWindow;
    private final JTextField firstOperand;
    private final JTextField secondOperand;
    private final JTextField resultField;
    private final JTextField operationsControl;

    
    /*
     * Constructor
     */
    public DynCalculator(String configurationFile) {

        try {
            configuration = new PropertyHandler(configurationFile);
        } catch (IOException e) {
            // no use carrying on, so bailing out ...
            System.err.println("No config file found, bailing out ...");
            System.exit(1);
        }

        // initialization of the class loader
        initOperationsLoader();

        // create GUI resources
        applicationWindow = new JFrame("Dynamic Calculator");

        firstOperand = new JTextField("0", 10);
        operationsControl = new JTextField("+", 1);
        secondOperand = new JTextField("0", 10);
        resultField = new JTextField("0", 20);
        resultField.setEditable(false);

        JPanel textFieldPanel = new JPanel();

        textFieldPanel.setLayout(new GridLayout(3, 1));
        textFieldPanel.add(firstOperand);
        textFieldPanel.add(secondOperand);
        textFieldPanel.add(resultField);

        applicationWindow.getContentPane().setLayout(new GridLayout(1, 2));
        applicationWindow.getContentPane().add(operationsControl);
        applicationWindow.getContentPane().add(textFieldPanel);

        // setting up event processing
        EventControlText controllerTextEvent = new EventControlText(this);
        EventControlWindow controllerWindowEvent = new EventControlWindow();

        operationsControl.getDocument().addDocumentListener(controllerTextEvent);
        firstOperand.getDocument().addDocumentListener(controllerTextEvent);
        secondOperand.getDocument().addDocumentListener(controllerTextEvent);
        applicationWindow.addWindowListener(controllerWindowEvent);

        // display application window
        applicationWindow.getContentPane().setLayout(new FlowLayout());
        applicationWindow.pack();
        applicationWindow.setVisible(true);
    }

    /*
     * Method for calculating the result
     * to be called from the controllerTextEvent
     */
    public void getResult() {

        // local variables, contain operands' numbers
        float firstNumber, secondNumber;

        // getting operands
        try {
            firstNumber = Float.parseFloat(firstOperand.getText());
            secondNumber = Float.parseFloat(secondOperand.getText());
        } catch (NumberFormatException e) {
            resultField.setText("ERROR IN NUMBER FORMAT!");
            return;
        }

        // calculating and displaying the result
        try {
            Operation operation = getOperation(operationsControl.getText());
            float result = operation.calculate(firstNumber, secondNumber);
            resultField.setText(Float.toString(result));
        } catch (UnknownOperationException | IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            resultField.setText("OPERATION NOT IMPLEMENTED!");
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DynCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("[DynCalculator] getResult() - NoSuchMethodException");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DynCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("[DynCalculator] getResult() - IllegalArgumentException");
        }
    }


    /*
     * Auxiliary method for loading a certain operation.
     * Contains a cache mechanism for operations' classes already loaded
     */
    private Operation getOperation(String operationString) throws IOException, UnknownOperationException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException 
    {

        Operation operationObject;

        if ((operationObject = operationsCache.get(operationString)) == null) 
        {
            String operationClassString = configuration.getProperty(operationString);
            System.out.println("\nOperation's Class: " + operationClassString);
            if (operationClassString == null) 
            {
                throw new UnknownOperationException();
            }

            Class<?> operationClass = operationsLoader.loadClass(operationClassString);
            try {
                operationObject = (Operation) operationClass.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DynCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("[DynCalculator] getOperation() - InvocationTargetException");
            }
            operationsCache.put(operationString, operationObject);
        } 
        else 
        {
            System.out.println("Operation: \"" + operationString + "\" already in Cache");
        }

        return operationObject;
    }

    
    /*
     * The bootstrap method <code>main()</code>
     */
    public static void main(String args[]) {
        new DynCalculator("../../config/DynCalculator.properties");
    }

    
    /**
     * Auxiliary method for initializing the class loader
     */
    private void initOperationsLoader() {

        String host = configuration.getProperty("HOST");
        String portString = configuration.getProperty("PORT");

        if ((host != null) && (portString != null)) {
            try {
                operationsLoader = new HTTPClassLoader(host, Integer.parseInt(portString));
            } catch (NumberFormatException nfe) {
                System.err.println("Wrong Portnumber, using Defaults");
            }
        } else {
            System.err.println("configuration data incomplete, using Defaults");
        }

        if (operationsLoader == null) {
            System.err.println("Could not create HTTPClassLoader, exiting ...");
            System.exit(1);
        }
    }
}
