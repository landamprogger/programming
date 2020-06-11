package view.main;

import controller.AbstractController;
import controller.localizer.Localizer;
import controller.serverAdapter.exception.ServerAdapterException;
import controller.serverAdapter.exception.ServerInternalErrorException;
import controller.serverAdapter.exception.ServerUnavailableException;
import controller.serverAdapter.exception.WrongQueryException;
import domain.exception.VerifyException;
import domain.studyGroup.FormOfEducation;
import domain.studyGroup.Semester;
import domain.studyGroup.StudyGroup;
import domain.studyGroup.coordinates.Coordinates;
import domain.studyGroup.dao.ServerStudyGroupDAO;
import domain.studyGroup.person.Country;
import domain.studyGroup.person.Person;
import domain.studyGroupRepository.StudyGroupCollectionUpdater;
import domain.studyGroupRepository.StudyGroupRepositorySubscriber;
import domain.user.ServerUserDAO;
import domain.user.User;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import manager.LogManager;
import view.fxController.FXController;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.sqrt;

public class MainController extends FXController implements StudyGroupRepositorySubscriber {
    private static final LogManager LOG_MANAGER = LogManager.createDefault(MainController.class);

    private static final String ENTER_VALUE_TO_FILTER = "Enter value to filter";
    private static final String MENU = "Menu";
    private static final String STUDY_GROUP = "Study group";
    private static final String USER_ID = "User ID";
    private static final String ID = "ID";
    private static final String COORDINATES = "Coordinates";
    private static final String NAME = "Name";
    private static final String CREATION_DATE = "Creation date";
    private static final String STUD_COUNT = "Students count";
    private static final String SHOULD_BE_EXPELLED = "Should be expelled";
    private static final String FORM_OF_EDUCATION = "Form of education";
    private static final String SEMESTER = "Semester";
    private static final String PERSON = "Person";
    private static final String HEIGHT = "Height";
    private static final String PASSPORT_ID = "Passport ID";
    private static final String NATIONALITY = "Nationality";

    private static final String X_COORDINATES = "Coordinates X";
    private static final String Y_COORDINATES = "Coordinates Y";
    private static final String STUDY_GROUP_NAME = "Study group name";
    private static final String PERSON_NAME = "Person name";
    @FXML
    public MenuBar menuBar;
    @FXML
    public TextField filter;
    @FXML
    public ChoiceBox<String> choice;
    @FXML
    public TableColumn<StudyGroup, Integer> studyGroup;
    @FXML
    public TableColumn<StudyGroup, Long> idCol;
    @FXML
    public TableColumn<StudyGroup, String> personNameCol;
    @FXML
    public TableColumn<StudyGroup, Integer> userIdCol;
    @FXML
    public TableColumn<StudyGroup, String> nameCol;
    @FXML
    public TableColumn<StudyGroup, Integer> coordinatesCol;
    @FXML
    public TableColumn<StudyGroup, Integer> xCoorCol;
    @FXML
    public TableColumn<StudyGroup, Integer> yCoorCol;
    @FXML
    public TableColumn<StudyGroup, String> creatDateCol;
    @FXML
    public TableColumn<StudyGroup, Integer> studCountCol;
    @FXML
    public TableColumn<StudyGroup, Long> shouldBeExpCol;
    @FXML
    public TableColumn<StudyGroup, FormOfEducation> formOfEducCol;
    @FXML
    public TableColumn<StudyGroup, Semester> semesterCol;
    @FXML
    public TableColumn<StudyGroup, Integer> heightCol;
    @FXML
    public TableColumn<StudyGroup, String> passportIdCol;
    @FXML
    public TableColumn<StudyGroup, Country> natCol;
    @FXML
    public TableView<StudyGroup> table;
    @FXML
    public Canvas canvasField;

    private ObservableList<StudyGroup> studyGroups;
    private ServerUserDAO serverUserDAO;
    private ServerStudyGroupDAO serverStudyGroupDAO;
    private User user;
    private StudyGroupCollectionUpdater studyGroupCollectionUpdater;


    public MainController(AbstractController businessLogicController,
                          ServerStudyGroupDAO serverStudyGroupDAO,
                          ServerUserDAO serverUserDAO) {

        super(businessLogicController);
        this.serverUserDAO = serverUserDAO;
        this.serverStudyGroupDAO = serverStudyGroupDAO;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     * <p>
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    private void initialize() {
        studyGroups = FXCollections.observableArrayList();
        initTableProperties();
        bindColumnsToProductFields();
        initContextMenu();

        canvasField.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double sceneX = event.getX() - MAGIC_CRUTCH_NUMBER;
            double sceneY = event.getY() - MAGIC_CRUTCH_NUMBER;

            for (StudyGroup studyGroup : studyGroups) {
                double centerX = studyGroup.getCoordinatesX();
                double centerY = studyGroup.getCoordinatesY();

                double length = sqrt((sceneX - centerX) * (sceneX - centerX) + (sceneY - centerY) * (sceneY - centerY));

                if (length <= 20.0) {
                    table.getSelectionModel().select(studyGroup);
                }
            }
        });

        /*Localizer.bindComponentToLocale(hasLocationButton, "MainScreen", "availabilityLocation");
        Localizer.bindComponentToLocale(hasOrganizationButton, "MainScreen", "availabilityOrganization");
        Localizer.bindComponentToLocale(filter, "MainScreen", "filter");
        Localizer.bindComponentToLocale(productProps, "MainScreen", "prodProps");

        Localizer.bindComponentToLocale(userIdColumn, "MainScreen", "userId");
        Localizer.bindComponentToLocale(productNameColumn, "MainScreen", "name");
        Localizer.bindComponentToLocale(priceColumn, "MainScreen", "price");
        Localizer.bindComponentToLocale(partNumberColumn, "MainScreen", "partNumber");
        Localizer.bindComponentToLocale(unitOfMeasureColumn, "MainScreen", "unitOfMeasure");
        Localizer.bindComponentToLocale(creationDateColumn, "MainScreen", "creationDate");
        Localizer.bindComponentToLocale(manufactureCostColumn, "MainScreen", "manufactureCost");
        Localizer.bindComponentToLocale(organizationColumn, "MainScreen", "organization");
        Localizer.bindComponentToLocale(productColumn, "MainScreen", "product");
        Localizer.bindComponentToLocale(addressColumn, "MainScreen", "address");
        Localizer.bindComponentToLocale(locationColumn, "MainScreen", "location");
        Localizer.bindComponentToLocale(orgNameColumn, "MainScreen", "name");
        Localizer.bindComponentToLocale(orgAnnualTurnoverColumn, "MainScreen", "anTur");
        Localizer.bindComponentToLocale(orgTypeColumn, "MainScreen", "type");
        Localizer.bindComponentToLocale(zipCodeColumn, "MainScreen", "zipCode");
        Localizer.bindComponentToLocale(coordinatesColumn, "MainScreen", "coordinates");*/
    }

    private <T> StudyGroup getStudyGroup(TableColumn.CellEditEvent<StudyGroup, T> event) {
        TablePosition<StudyGroup, T> position = event.getTablePosition();
        int row = position.getRow();
        return event.getTableView().getItems().get(row);
    }

    private Timer canvasTimer;
    @Override
    public void onStart() {
        sceneAdapter.getStage().setFullScreen(true);
         studyGroupCollectionUpdater.start();

        initStudyGroupCollection();

        bindCellsToTextEditors();

        try {
            user = serverUserDAO.get(screenContext.get("login"));
        } catch (ServerAdapterException e) {
            handleServerAdapterException(e);
        }

        initUserColors();
        canvasTimer = new Timer();
        canvasTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCanvas();
            }
        }, 100, 1000);
    }

    /*private void initMenuBar() {
        ImageView imageView = new ImageView(new Image(ViewportController.class.getResourceAsStream("/pictures/settings.jpg")));
        imageView.setFitHeight(17);
        imageView.setFitWidth(17);

        menu.setGraphic(imageView);

        MenuItem profile = new MenuItem(Localizer.getStringFromBundle("profile", "ViewportScreen"));
        Localizer.bindComponentToLocale(profile, "ViewportScreen", "profile");
        profile.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCodeCombination.CONTROL_DOWN));
        profile.setOnAction(event -> {
            Stage newWindow = new Stage();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("markup/profile.fxml");
            loader.setLocation(url);

            ProfileController profileController = new ProfileController(user);
            loader.setController(profileController);

            Parent parent = null;
            try {
                parent = loader.load();
            } catch (IOException e) {
                showInternalErrorAlert(Localizer.getStringFromBundle("errorDuling", "ViewportScreen"));
            }

            newWindow.setScene(new Scene(parent));
            newWindow.setTitle("Profile");
            newWindow.show();
        });

        MenuItem settings = new MenuItem(Localizer.getStringFromBundle("settings", "ViewportScreen"));
        Localizer.bindComponentToLocale(settings, "ViewportScreen", "settings");
        settings.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN));
        settings.setOnAction(event -> {
            Stage newWindow = new Stage();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("markup/settings.fxml");
            loader.setLocation(url);

            //SettingsController settingsController = new SettingsController(tableController);
            //loader.setController(settingsController);

            Parent parent = null;
            try {
                parent = loader.load();
            } catch (IOException e) {
                //todo todo
                //showInternalErrorAlert(Localizer.getStringFromBundle("errorDuling", "ViewportScreen"));
            }

            newWindow.setScene(new Scene(parent));
            newWindow.setTitle("Profile");
            newWindow.show();
        });

        MenuItem logout = new MenuItem(Localizer.getStringFromBundle("logOut", "ViewportScreen"));
        Localizer.bindComponentToLocale(logout, "ViewportScreen", "logOut");
        logout.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN));
        logout.setOnAction(event -> {
            screenContext.remove("accessToken");
            onStop();
            LOGGER_ADAPTER.info("All support threads has been stop");
            screenContext.getRouter().go("signIn");
        });

        MenuItem refreshCollection = new MenuItem(Localizer.getStringFromBundle("refreshData", "ViewportScreen"));
        Localizer.bindComponentToLocale(refreshCollection, "ViewportScreen", "refreshData");
        refreshCollection.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN));
        refreshCollection.setOnAction(event -> {
            try {
                tableController.change(serverProductDAO.get(new GetAllProductSortedById()));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();

        MenuItem exit = new MenuItem(Localizer.getStringFromBundle("exit", "ViewportScreen"));
        Localizer.bindComponentToLocale(exit, "ViewportScreen", "exit");
        exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN));
        exit.setOnAction(event -> {
            try {
                screenContext.save();
            } catch (IOException e) {
                LOGGER_ADAPTER.errorThrowable(e);
            }

            System.exit(0);
        });

        menu.getItems().addAll(profile, settings, separatorMenuItem, refreshCollection, separatorMenuItem1, logout, exit);
    }*/

    private void bindCellsToTextEditors() {
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, String> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            try {
                getStudyGroup(event).setName(event.getNewValue());
            } catch (VerifyException e) {
                table.refresh();
                showErrorAlert(Localizer.getStringFromBundle("noteStudyGroup", "MainScreen"));
            }

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        xCoorCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        xCoorCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Integer> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getCoordinates().setX(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        yCoorCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        yCoorCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Integer> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getCoordinates().setY(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        studCountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        studCountCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Integer> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).setStudentsCount(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        shouldBeExpCol.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        shouldBeExpCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Long> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).setShouldBeExpelled(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        formOfEducCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(FormOfEducation.values())));
        formOfEducCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, FormOfEducation> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).setFormOfEducation(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        semesterCol
                .setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(Semester.values())));
        semesterCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Semester> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).setSemesterEnum(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        personNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        personNameCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, String> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getGroupAdmin().setName(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        heightCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        heightCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Integer> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getGroupAdmin().setHeight(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        passportIdCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passportIdCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, String> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getGroupAdmin().setPassportID(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });

        natCol
                .setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(Country.values())));
        natCol.setOnEditCommit((TableColumn.CellEditEvent<StudyGroup, Country> event) -> {
            if (getStudyGroup(event).getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            getStudyGroup(event).getGroupAdmin().setNationality(event.getNewValue());

            change(studyGroups);

            try {
                serverStudyGroupDAO.update(getStudyGroup(event));
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }
        });
    }

    private void initContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteStudyGroup = new MenuItem("delete");
        deleteStudyGroup.setOnAction(event -> {
            StudyGroup selectedItem = table.getSelectionModel().getSelectedItem();
            List<StudyGroup> localList = new ArrayList<>(studyGroups);

            if (selectedItem == null) {
                showErrorAlert(Localizer.getStringFromBundle("noteDelete", "MainScreen"));
                return;
            }

            if (selectedItem.getUserId() != user.getId()) {
                showWarningAlert(Localizer.getStringFromBundle("notYoursDelete", "MainScreen"));
                return;
            }

            localList.remove(selectedItem);

            studyGroups = FXCollections.observableArrayList(localList);
            table.setItems(studyGroups);

            try {
                serverStudyGroupDAO.delete(selectedItem.getId());
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }

            refreshFilterText();
        });

        MenuItem addGroup = new MenuItem("add");
        addGroup.setOnAction(event -> {
            Person person;
            try {
                person = new Person("1", 1, "1", Country.JAPAN);
            } catch (VerifyException e) {
                LOG_MANAGER.errorThrowable(e);
                throw new RuntimeException(e);
            }

            StudyGroup studyGroup;
            try {
                studyGroup = new StudyGroup(1L,
                                                    1,
                                                    "a",
                                                    new Coordinates(1,1 ),
                                                    LocalDateTime.now(),
                                                    1,
                                                    1L,
                                                    FormOfEducation.DISTANCE_EDUCATION,
                                                    Semester.EIGHTH,
                                                    person);
            } catch (VerifyException e) {
                LOG_MANAGER.errorThrowable(e);
                throw new RuntimeException(e);
            }

            List<StudyGroup> localList = new ArrayList<>(studyGroups);

            localList.add(studyGroup);

            studyGroups = FXCollections.observableArrayList(localList);
            table.setItems(studyGroups);

            try {
                serverStudyGroupDAO.create(studyGroup);
            } catch (ServerAdapterException e) {
                handleServerAdapterException(e);
            }

            refreshFilterText();
        });

        contextMenu.getItems().add(deleteStudyGroup);
        contextMenu.getItems().add(addGroup);

        table.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(table, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void initFilter(FilteredList<StudyGroup> filteredList) {
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(studyGroup -> {
                if (newValue == null || newValue.isEmpty()) {
                    return false;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                String filterProperty = choice.getValue();

                if (ID.equals(filterProperty)) {
                    return Long.toString(studyGroup.getId()).toLowerCase().contains(lowerCaseFilter);
                } else if (USER_ID.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getUserId()).contains(lowerCaseFilter);
                } else if (STUDY_GROUP_NAME.equals(filterProperty)) {
                    return studyGroup.getName().toLowerCase().contains(lowerCaseFilter);
                } else if (CREATION_DATE.equals(filterProperty)) {
                    return studyGroup.getCreationDate().toLowerCase().contains(lowerCaseFilter);
                } else if (X_COORDINATES.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getCoordinatesX()).toLowerCase().contains(lowerCaseFilter);
                } else if (Y_COORDINATES.equals(filterProperty)) {
                    return Double.toString(studyGroup.getCoordinatesY()).toLowerCase().contains(lowerCaseFilter);
                } else if (STUD_COUNT.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getStudentsCount()).toLowerCase().contains(lowerCaseFilter);
                } else if (SHOULD_BE_EXPELLED.equals(filterProperty)) {
                    return Long.toString(studyGroup.getShouldBeExpelled()).toLowerCase().contains(lowerCaseFilter);
                } else if (FORM_OF_EDUCATION.equals(filterProperty)) {
                    return studyGroup.getFormOfEducation().getName().toLowerCase().contains(lowerCaseFilter);
                } else if (SEMESTER.equals(filterProperty)) {
                    return studyGroup.getSemesterEnum().getName().toLowerCase().contains(lowerCaseFilter);
                } else if (PERSON_NAME.equals(filterProperty)) {
                    return studyGroup.getPersonName().toLowerCase().contains(lowerCaseFilter);
                } else if (HEIGHT.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getPersonHeight()).toLowerCase().contains(lowerCaseFilter);
                } else if (PASSPORT_ID.equals(filterProperty)) {
                    return studyGroup.getPersonPassportID().toLowerCase().contains(lowerCaseFilter);
                } else if (NATIONALITY.equals(filterProperty)) {
                    return studyGroup.getPersonNationality().getName().toLowerCase().contains(lowerCaseFilter);
                }
                return false;
            });
        });
    }

    private void initStudyGroupCollection() {
        ObservableList<StudyGroup> rawProducts = null;
        try {
            rawProducts = FXCollections.observableArrayList(serverStudyGroupDAO.get());
        } catch (ServerAdapterException e) {
            handleServerAdapterException(e);
        }

        FilteredList<StudyGroup> filteredList = new FilteredList<>(rawProducts, product -> true);
        initFilter(filteredList);

        SortedList<StudyGroup> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        studyGroups = sortedList;

        table.setItems(studyGroups);
    }

    private static final int MAGIC_CRUTCH_NUMBER = 500;

    private final Map<Integer, Color> userColors = new HashMap<>();
    private final Random random = new Random();
    //private final Map<Color, Circle> tooltips = new HashMap<>();

    private static class Point {
        int userId;
        double x, y;


        Point(int userId, double x, double y) {
            this.userId = userId;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "userId=" + userId +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private void updateCanvas() {
        GraphicsContext graphicsContext = canvasField.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvasField.getWidth(), canvasField.getHeight());

        DoubleProperty alpha  = new SimpleDoubleProperty(1.0);

        double maxAlpha = 1.0;

        List<Point> points = new ArrayList<>();
        studyGroups.forEach(studyGroup -> points.add(new Point(studyGroup.getUserId(), studyGroup.getCoordinates().getX(), studyGroup.getCoordinates().getY())));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(alpha, 0)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(alpha, maxAlpha)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(1);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                points.forEach(point -> {
                    GraphicsContext graphicsContext = canvasField.getGraphicsContext2D();

                    Color color = userColors.get(point.userId);
                    Color withAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha.doubleValue());
                    Circle circle = new Circle(point.x + MAGIC_CRUTCH_NUMBER,
                            point.y + MAGIC_CRUTCH_NUMBER,
                            20.0);

                    graphicsContext.setFill(withAlpha);
                    graphicsContext.fillOval(circle.getCenterX() - 20.0,
                            circle.getCenterY() - 20.0,
                            20.0 * 2,
                            20.02 * 2);

                    if (alpha.doubleValue() == maxAlpha) {
                        stop();
                    }
                });
            }
        };

        timer.start();
        timeline.play();
    }

    private void initUserColors() {
        List<User> users = new ArrayList<>();
        try {
            users = serverUserDAO.getAllUser();
        } catch (ServerAdapterException e) {
            handleServerAdapterException(e);
        }

        if (!users.isEmpty()) {
            users.forEach(concreteUser -> userColors.put(concreteUser.getId(), generateRandomColor()));
        }
    }

    private Color generateRandomColor() {
        return new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1);
    }

    private void handleServerAdapterException(ServerAdapterException serverAdapterException) {
        if (serverAdapterException instanceof ServerInternalErrorException) {
            showInternalErrorAlert(Localizer.getStringFromBundle("serverAnswerInternalError", "MainScreen"));
            System.exit(1);
        }

        if (serverAdapterException instanceof ServerUnavailableException) {
            showDisconnectAlert();
        }

        if (serverAdapterException instanceof WrongQueryException) {
            showInternalErrorAlert(Localizer.getStringFromBundle("serverAnswerBadRequest", "MainScreen"));
            System.exit(1);
        }
    }

    private void showInternalErrorAlert(String string) {
        if (alert == null) {
            alert = new Alert(Alert.AlertType.ERROR, string);
            alert.showAndWait();
            alert = null;
        }
    }

    private void showWarningAlert(String errorText) {
        if (alert == null) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Storage");
            alert.setContentText(errorText);
            alert.showAndWait();
            alert = null;
        }
    }

    private Alert alert;

    private void showDisconnectAlert() {
        if (alert != null) {
            return;
        }

        alert = new Alert(Alert.AlertType.CONFIRMATION,
                Localizer.getStringFromBundle("disconnectFormServer", "MainScreen"),
                ButtonType.FINISH, ButtonType.OK);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> reconnectToServer());
            }
        }, 30000);

        Optional<ButtonType> response = alert.showAndWait();
        response.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                alert = null;
                reconnectToServer();
            }

            if (buttonType.equals(ButtonType.FINISH)) {
                alert = null;
                System.exit(0);
            }
            alert = null;
        });
    }

    private void reconnectToServer() {
        try {
            if (serverStudyGroupDAO.checkConnection()) {
                alert = new Alert(Alert.AlertType.INFORMATION, Localizer.getStringFromBundle("successfullyReconnected", "MainScreen"));
                alert.showAndWait();
                alert = null;
            }
        } catch (ServerAdapterException e) {
            handleServerAdapterException(e);
        }
    }

    private void initTableProperties() {
        // todo wew
        //table.setPlaceholder(new Label(Localizer.getStringFromBundle("noProducts", "TableScreen")));
        table.setEditable(true);

        initChoiceBox();
    }

    private void bindColumnsToProductFields() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creatDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        xCoorCol.setCellValueFactory(new PropertyValueFactory<>("coordinatesX"));
        yCoorCol.setCellValueFactory(new PropertyValueFactory<>("coordinatesY"));
        studCountCol.setCellValueFactory(new PropertyValueFactory<>("studentsCount"));
        shouldBeExpCol.setCellValueFactory(new PropertyValueFactory<>("shouldBeExpelled"));
        formOfEducCol.setCellValueFactory(new PropertyValueFactory<>("formOfEducation"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semesterEnum"));
        personNameCol.setCellValueFactory(new PropertyValueFactory<>("personName"));
        heightCol.setCellValueFactory(new PropertyValueFactory<>("personHeight"));
        passportIdCol.setCellValueFactory(new PropertyValueFactory<>("personPassportID"));
        natCol.setCellValueFactory(new PropertyValueFactory<>("personNationality"));
    }

    private void initChoiceBox() {
        ObservableList<String> choices = FXCollections.observableArrayList(
                ID,
                USER_ID,
                STUDY_GROUP_NAME,
                CREATION_DATE,
                X_COORDINATES,
                Y_COORDINATES,
                STUD_COUNT,
                SHOULD_BE_EXPELLED,
                FORM_OF_EDUCATION,
                SEMESTER,
                PERSON_NAME,
                HEIGHT,
                PASSPORT_ID,
                NATIONALITY
        );

        choice.setItems(choices);
        choice.getSelectionModel().select(0);
    }

    private void refreshFilterText() {
        String oldFilterText = filter.getText();
        filter.textProperty().setValue(" ");
        filter.textProperty().setValue(oldFilterText);
    }

    public void setStudyGroupCollectionUpdater(StudyGroupCollectionUpdater studyGroupCollectionUpdater) {
        this.studyGroupCollectionUpdater = studyGroupCollectionUpdater;
    }

    @Override
    public void change(List<StudyGroup> products) {
        ObservableList<StudyGroup> rawProducts = FXCollections.observableArrayList(products);
        FilteredList<StudyGroup> filteredList = new FilteredList<>(rawProducts, product -> true);

        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(studyGroup -> {
                if (newValue == null || newValue.isEmpty()) {
                    return false;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                String filterProperty = choice.getValue();

                if (ID.equals(filterProperty)) {
                    return Long.toString(studyGroup.getId()).toLowerCase().contains(lowerCaseFilter);
                } else if (USER_ID.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getUserId()).contains(lowerCaseFilter);
                } else if (STUDY_GROUP_NAME.equals(filterProperty)) {
                    return studyGroup.getName().toLowerCase().contains(lowerCaseFilter);
                } else if (CREATION_DATE.equals(filterProperty)) {
                    return studyGroup.getCreationDate().toLowerCase().contains(lowerCaseFilter);
                } else if (X_COORDINATES.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getCoordinatesX()).toLowerCase().contains(lowerCaseFilter);
                } else if (Y_COORDINATES.equals(filterProperty)) {
                    return Double.toString(studyGroup.getCoordinatesY()).toLowerCase().contains(lowerCaseFilter);
                } else if (STUD_COUNT.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getStudentsCount()).toLowerCase().contains(lowerCaseFilter);
                } else if (SHOULD_BE_EXPELLED.equals(filterProperty)) {
                    return Long.toString(studyGroup.getShouldBeExpelled()).toLowerCase().contains(lowerCaseFilter);
                } else if (FORM_OF_EDUCATION.equals(filterProperty)) {
                    return studyGroup.getFormOfEducation().getName().toLowerCase().contains(lowerCaseFilter);
                } else if (SEMESTER.equals(filterProperty)) {
                    return studyGroup.getSemesterEnum().getName().toLowerCase().contains(lowerCaseFilter);
                } else if (PERSON_NAME.equals(filterProperty)) {
                    return studyGroup.getPersonName().toLowerCase().contains(lowerCaseFilter);
                } else if (HEIGHT.equals(filterProperty)) {
                    return Integer.toString(studyGroup.getPersonHeight()).toLowerCase().contains(lowerCaseFilter);
                } else if (PASSPORT_ID.equals(filterProperty)) {
                    return studyGroup.getPersonPassportID().toLowerCase().contains(lowerCaseFilter);
                } else if (NATIONALITY.equals(filterProperty)) {
                    return studyGroup.getPersonNationality().getName().toLowerCase().contains(lowerCaseFilter);
                }
                return false;
            });
        });

        SortedList<StudyGroup> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        this.studyGroups = sortedList;

        table.setItems(sortedList);
        Platform.runLater(this::refreshFilterText);
    }

    @Override
    public void disconnect() {

    }

    public void onStop() {
        if (studyGroupCollectionUpdater != null) {
            studyGroupCollectionUpdater.stop();
        }

        if (canvasTimer != null) {
            canvasTimer.cancel();
        }
    }
}
