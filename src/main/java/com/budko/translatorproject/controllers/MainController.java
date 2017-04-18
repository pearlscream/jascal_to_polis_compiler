package com.budko.translatorproject.controllers;


import com.budko.translatorproject.abstraction.DataSource;
import com.budko.translatorproject.entities.*;
import com.budko.translatorproject.logic.LexicalAnalyzer;
import com.budko.translatorproject.logic.SyntaxAnalyzer;
import com.budko.translatorproject.logic.SyntaxAnalyzerAscending;
import com.budko.translatorproject.utils.FileDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * @author DBudko.
 */
public class MainController {

    private Lexeme lexeme;
    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;

    @FXML
    private TableView lexemeTableView;
    @FXML
    private TableView idTableView;
    @FXML
    private TableColumn idCodeColumn;
    @FXML
    private TableColumn idName;
    @FXML
    private TableColumn idType;
    @FXML
    private TableColumn recordNumber;
    @FXML
    private TableColumn lineNumber;
    @FXML
    private TableColumn lexemeName;
    @FXML
    private TableColumn lexemeCode;
    @FXML
    private TableColumn idCode;
    @FXML
    private TableView constantTableView;
    @FXML
    private TableColumn constantCode;
    @FXML
    private TableColumn constantName;
    @FXML
    private TableColumn constantCodeColumn;
    @FXML
    private TableColumn constantNameColumn;
    @FXML
    private AnchorPane ap;
    @FXML
    private TextArea programArea;
    @FXML
    private Text exceptionText;

    @FXML
    private TableView<AscendingEntity> ascTable;
    @FXML
    private TableColumn<AscendingEntity, Integer> ascStep;
    @FXML
    private TableColumn<AscendingEntity, String> ascStack;
    @FXML
    private TableColumn<AscendingEntity, String> ascSign;
    @FXML
    private TableColumn<AscendingEntity, String> ascInput;
    @FXML
    private TableColumn<AscendingEntity, String> poliz;


    @FXML
    private void initialize() {

    }

    @FXML
    public void startProgram(ActionEvent event) {
        lexicalAnalyzer = new LexicalAnalyzer(programArea.getText());
//        try {
        exceptionText.setText("");
        Lexeme.setLexemesToZero();
        Identifier.setIdentifiersToZero();
        Constant.setConstantsToZero();
        lexicalAnalyzer.analyze();
        recordNumber.setCellValueFactory(new PropertyValueFactory<Lexeme, Integer>("lexemeNumber"));
        lineNumber.setCellValueFactory(new PropertyValueFactory<Lexeme, Integer>("lineNumber"));
        lexemeName.setCellValueFactory(new PropertyValueFactory<Lexeme, String>("lexemeName"));
        lexemeCode.setCellValueFactory(new PropertyValueFactory<Lexeme, Integer>("lexemeCode"));
        idCode.setCellValueFactory(new PropertyValueFactory<Lexeme, Integer>("idnCode"));
        idCodeColumn.setCellValueFactory(new PropertyValueFactory<Identifier, Integer>("code"));
        idName.setCellValueFactory(new PropertyValueFactory<Identifier, String>("name"));
        idType.setCellValueFactory(new PropertyValueFactory<Identifier, String>("type"));
        constantCodeColumn.setCellValueFactory(new PropertyValueFactory<Constant, Integer>("code"));
        constantNameColumn.setCellValueFactory(new PropertyValueFactory<Constant, String>("value"));

        ascStep.setCellValueFactory(new PropertyValueFactory<>("step"));
        ascStack.setCellValueFactory(new PropertyValueFactory<>("stack"));
        ascSign.setCellValueFactory(new PropertyValueFactory<>("sign"));
        ascInput.setCellValueFactory(new PropertyValueFactory<>("input"));
        poliz.setCellValueFactory(new PropertyValueFactory<>("poliz"));





        ObservableList<Lexeme> outputTable = FXCollections.observableArrayList(lexicalAnalyzer.getOutputLexemeTable());
        lexemeTableView.setItems(outputTable);
        ObservableList<Identifier> identifiersTable = FXCollections.observableArrayList(lexicalAnalyzer.getIdentifiers());
        idTableView.setItems(identifiersTable);
        ObservableList<Constant> constantsTable = FXCollections.observableArrayList(lexicalAnalyzer.getConstants());
        constantTableView.setItems(constantsTable);
        lexicalAnalyzer.printOutputLexemeTable();

        ascTable.getItems().clear();
        try {
            String grammarPath = "d:\\labs_6_semestr\\sapr\\2nd_lab\\TranslatorProject\\grammarTable.txt";
            SyntaxAnalyzerAscending syntaxAnalyzerAscending = new SyntaxAnalyzerAscending(lexicalAnalyzer.getOutputLexemeTable(),grammarPath);
            for (AscendingEntity ascendingEntity : syntaxAnalyzerAscending.getAscendingEntities()) {
                System.out.println("----------------------------------------------------------------------------");
                System.out.println("Step = " + ascendingEntity.getStep());
                System.out.println("Stack = " + ascendingEntity.getStack());
                System.out.println("Sign = " + ascendingEntity.getSign());
                System.out.println("Input = " + ascendingEntity.getInput());
                System.out.println("Poliz = " + ascendingEntity.getPoliz());

            }
            ObservableList<AscendingEntity> ascendingEntity = FXCollections.observableArrayList(syntaxAnalyzerAscending.getAscendingEntities());
            ascTable.setItems(ascendingEntity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//            System.out.println("Syntax analyzer");
//            syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer.getOutputLexemeTable());
//            syntaxAnalyzer.analyze();
//            for (Error error : syntaxAnalyzer.getErrors()) {
//                exceptionText.setText(exceptionText.getText() + "\n" + "  Рядок " + error.getLineNumber()  + " " + error.getText());
//            }
//        }
//        catch (IllegalArgumentException e) {
//            exceptionText.setText("\n " + e.getMessage());
//        }
    }

    @FXML
    public void fileUpload(ActionEvent event) {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jascal Files", "*.jas"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialDirectory(new File("d:\\labs_6_semestr\\sapr\\2nd_lab\\TranslatorProject\\"));
        File uploadedFile = fileChooser.showOpenDialog(stage);
        DataSource dataSource = new FileDataSource(uploadedFile.getPath());
        programArea.setText(dataSource.getProgramText());
    }

}
