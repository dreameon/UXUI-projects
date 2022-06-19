import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.scene.shape.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.util.*

// CREATION OF THE RECTANGLES AND OTHER SHAPES WAS TAKEN INSPIRATION FROM THE SHAPE DEMO IN THE CLASS REPO

// Displays the canvas for the tool
internal class Menu(
    private val model: Model
) : MenuBar(), IView {
    private var shapeGroup = Group()
    private var selectedShape: Shape? = null
    private var cbShape: Shape? = null
    private var needsSave = false
    private val fileMenu = Menu("File")
    private val fileNew = MenuItem("New")
    private val fileLoad = MenuItem("Load")
    private val fileSave = MenuItem("Save")
    private val fileQuit = MenuItem("Quit")
    private val helpMenu = Menu("Help")
    private val helpAbout = MenuItem("About")
    private val editMenu = Menu("Edit")
    private val editCut = MenuItem("Cut")
    private val editCopy = MenuItem("Copy")
    private val editPaste = MenuItem("Paste")
    private val fileChooser = FileChooser()

    fun serialize(shape: Shape): String {
        var hackShape: String = ""
        when (shape) {
            is Line -> {
                hackShape += "Line\n"
                hackShape += "${shape.startX}\n"
                hackShape += "${shape.startY}\n"
                hackShape += "${shape.endX}\n"
                hackShape += "${shape.endY}\n"
                hackShape += "${shape.stroke}\n"
                hackShape += "${shape.strokeWidth}\n"
                hackShape += "${shape.strokeDashArray}\n"
            }
            is Circle -> {
                hackShape += "Circle\n"
                hackShape += "${shape.centerX}\n"
                hackShape += "${shape.centerY}\n"
                hackShape += "${shape.radius}\n"
                hackShape += "${shape.fill}\n"
                hackShape += "${shape.stroke}\n"
                hackShape += "${shape.strokeWidth}\n"
                hackShape += "${shape.strokeDashArray}\n"
            }
            is Rectangle -> {
                hackShape += "Rectangle\n"
                hackShape += "${shape.x}\n"
                hackShape += "${shape.y}\n"
                hackShape += "${shape.width}\n"
                hackShape += "${shape.height}\n"
                hackShape += "${shape.fill}\n"
                hackShape += "${shape.stroke}\n"
                hackShape += "${shape.strokeWidth}\n"
                hackShape += "${shape.strokeDashArray}\n"
            }
        }
        return hackShape
    }

    override fun updateView() {
        selectedShape = model.selectedShape
        shapeGroup = model.shapeGroup
        cbShape = model.copiedShape
        needsSave = model.save
        if (model.selectedShape == null) {
            editCut.isDisable = true
            editCopy.isDisable = true
            editPaste.isDisable = true
        } else {
            editCut.isDisable = false
            editCopy.isDisable = false
            editPaste.isDisable = false
        }
    }

    init {
        fileMenu.items.addAll(fileNew, fileLoad, fileSave, fileQuit)
        editMenu.items.addAll(editCut, editCopy, editPaste)
        helpMenu.items.addAll(helpAbout)
        fileChooser.title = "Save As"
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("SketchIt Files", "*.sketch"))
        fileChooser.initialDirectory = File(System.getProperty("user.dir"))
        editCut.setOnAction { event ->
            selectedShape?.let { model.cutShape(it) }
        }
        editCopy.setOnAction { event ->
            selectedShape?.let { model.copyShape(it) }
        }
        editPaste.setOnAction { event ->
            selectedShape?.let { model.pasteShape(it) }
        }
        fileQuit.setOnAction { event ->
            if (needsSave) {
                val saveButton = ButtonType("Save", ButtonBar.ButtonData.YES)
                val noSaveButton = ButtonType("Quit Without Saving", ButtonBar.ButtonData.NO)
                val cancelButton = ButtonType("Cancel Operation", ButtonBar.ButtonData.CANCEL_CLOSE)
                val confirmQuit = Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Do you want to save before quit the application?",
                    noSaveButton,
                    saveButton,
                    cancelButton
                )
                confirmQuit.headerText = "Confirm Quit Without Saving"
                confirmQuit.title = "Confirm Quit Without Saving"
                val result = confirmQuit.showAndWait()
                if (result.isPresent && result.get() == saveButton) {
                    val promptWindow = Stage()
                    val saveFile = fileChooser.showSaveDialog(promptWindow)
                    saveFile?.let {
                        var hackShapeList = ""
                        for (shape in shapeGroup.children) {
                            hackShapeList += serialize(shape as Shape)
                        }
                        saveFile.writeText(hackShapeList)
                    }
                } else if (result.get() == noSaveButton) {
                    model.clearCanvas()
                    Platform.exit()
                }
            } else {
                model.clearCanvas()
                Platform.exit()
            }
        }
        fileSave.setOnAction {
            val promptWindow = Stage()
            val saveFile = fileChooser.showSaveDialog(promptWindow)
            saveFile?.let {
                var hackShapeList = ""
                for (shape in shapeGroup.children) {
                    hackShapeList += serialize(shape as Shape)
                }
                saveFile.writeText(hackShapeList)
            }
            model.saved()
        }
        fileNew.setOnAction {
            if (needsSave) {
                val saveButton = ButtonType("Save", ButtonBar.ButtonData.YES)
                val noSaveButton = ButtonType("Create New File Without Saving", ButtonBar.ButtonData.NO)
                val cancelButton = ButtonType("Cancel Operation", ButtonBar.ButtonData.CANCEL_CLOSE)
                val confirmNew = Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Do you want to save before you create a new file?",
                    saveButton,
                    noSaveButton,
                    cancelButton
                )
                confirmNew.headerText = "Confirm New File Creation"
                confirmNew.title = "Confirm New File Creation"
                val result = confirmNew.showAndWait()
                if (result.isPresent) {
                    if (result.get() == saveButton) {
                        val promptWindow = Stage()
                        val saveFile = fileChooser.showSaveDialog(promptWindow)
                        saveFile?.let {
                            var hackShapeList = ""
                            for (shape in shapeGroup.children) {
                                hackShapeList += serialize(shape as Shape)
                            }
                            saveFile.writeText(hackShapeList)
                        }
                    } else if (result.get() == noSaveButton) {
                        model.clearCanvas()
                    }
                }
            } else {
                model.clearCanvas()
            }
        }
        fileLoad.setOnAction {
            if (needsSave) {
                val saveButton = ButtonType("Save", ButtonBar.ButtonData.YES)
                val noSaveButton = ButtonType("Load File Without Saving", ButtonBar.ButtonData.NO)
                val cancelButton = ButtonType("Cancel Operation", ButtonBar.ButtonData.CANCEL_CLOSE)
                val confirmLoad = Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Do you want to save before you load a different file?",
                    noSaveButton,
                    saveButton,
                    cancelButton
                )
                confirmLoad.headerText = "Confirm File Load Action"
                confirmLoad.title = "Confirm File Load Action"
                val result = confirmLoad.showAndWait()
                if (result.isPresent && result.get() == saveButton) {
                    val promptWindow = Stage()
                    val saveFile = fileChooser.showSaveDialog(promptWindow)
                    saveFile?.let {
                        var hackShapeList = ""
                        for (shape in shapeGroup.children) {
                            hackShapeList += serialize(shape as Shape)
                        }
                        saveFile.writeText(hackShapeList)
                    }
                } else if (result.get() == noSaveButton) {
                    val promptWindow = Stage()
                    val loadFile = fileChooser.showOpenDialog(promptWindow)
                    var fileContents = ""
                    loadFile?.let {
                        model.clearCanvas()
                        val loadFileReader = Scanner(loadFile)
                        while (loadFileReader.hasNext()) {
                            fileContents = loadFileReader.nextLine()
                            when (fileContents) {
                                "Line" -> {
                                    val newShape = Line()
                                    newShape.startX = loadFileReader.nextLine().toDouble()
                                    newShape.startY = loadFileReader.nextLine().toDouble()
                                    newShape.endX = loadFileReader.nextLine().toDouble()
                                    newShape.endY = loadFileReader.nextLine().toDouble()
                                    newShape.stroke = Color.web(loadFileReader.nextLine())
                                    newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                    var fileDashArray = loadFileReader.nextLine()
                                    fileDashArray =
                                        fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                    if (fileDashArray != "") {
                                        var dasharrayStr = fileDashArray.split(",")
                                        for (dash in dasharrayStr) {
                                            newShape.strokeDashArray.add(dash.toDouble())
                                        }
                                    }
                                    model.addShape(newShape)
                                }
                                "Circle" -> {
                                    val newShape = Circle()
                                    newShape.centerX = loadFileReader.nextLine().toDouble()
                                    newShape.centerY = loadFileReader.nextLine().toDouble()
                                    newShape.radius = loadFileReader.nextLine().toDouble()
                                    newShape.fill = Color.web(loadFileReader.nextLine())
                                    newShape.stroke = Color.web(loadFileReader.nextLine())
                                    newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                    var fileDashArray = loadFileReader.nextLine()
                                    fileDashArray =
                                        fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                    if (fileDashArray != "") {
                                        var dasharrayStr = fileDashArray.split(",")
                                        for (dash in dasharrayStr) {
                                            newShape.strokeDashArray.add(dash.toDouble())
                                        }
                                    }
                                    model.addShape(newShape)
                                }
                                "Rectangle" -> {
                                    val newShape = Rectangle()
                                    newShape.x = loadFileReader.nextLine().toDouble()
                                    newShape.y = loadFileReader.nextLine().toDouble()
                                    newShape.width = loadFileReader.nextLine().toDouble()
                                    newShape.height = loadFileReader.nextLine().toDouble()
                                    newShape.fill = Color.web(loadFileReader.nextLine())
                                    newShape.stroke = Color.web(loadFileReader.nextLine())
                                    newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                    var fileDashArray = loadFileReader.nextLine()
                                    fileDashArray =
                                        fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                    if (fileDashArray != "") {
                                        var dasharrayStr = fileDashArray.split(",")
                                        for (dash in dasharrayStr) {
                                            newShape.strokeDashArray.add(dash.toDouble())
                                        }
                                    }
                                    model.addShape(newShape)
                                }
                            }
                        }
                        model.needsToLoad()
                        model.saved()
                    }
                }
            } else {
                val promptWindow = Stage()
                val loadFile = fileChooser.showOpenDialog(promptWindow)
                var fileContents = ""
                loadFile?.let {
                    model.clearCanvas()
                    val loadFileReader = Scanner(loadFile)
                    while (loadFileReader.hasNext()) {
                        fileContents = loadFileReader.nextLine()
                        when (fileContents) {
                            "Line" -> {
                                val newShape = Line()
                                newShape.startX = loadFileReader.nextLine().toDouble()
                                newShape.startY = loadFileReader.nextLine().toDouble()
                                newShape.endX = loadFileReader.nextLine().toDouble()
                                newShape.endY = loadFileReader.nextLine().toDouble()
                                newShape.stroke = Color.web(loadFileReader.nextLine())
                                newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                var fileDashArray = loadFileReader.nextLine()
                                fileDashArray =
                                    fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                if (fileDashArray != "") {
                                    var dasharrayStr = fileDashArray.split(",")
                                    for (dash in dasharrayStr) {
                                        newShape.strokeDashArray.add(dash.toDouble())
                                    }
                                }
                                model.addShape(newShape)
                            }
                            "Circle" -> {
                                val newShape = Circle()
                                newShape.centerX = loadFileReader.nextLine().toDouble()
                                newShape.centerY = loadFileReader.nextLine().toDouble()
                                newShape.radius = loadFileReader.nextLine().toDouble()
                                newShape.fill = Color.web(loadFileReader.nextLine())
                                newShape.stroke = Color.web(loadFileReader.nextLine())
                                newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                var fileDashArray = loadFileReader.nextLine()
                                fileDashArray =
                                    fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                if (fileDashArray != "") {
                                    var dasharrayStr = fileDashArray.split(",")
                                    for (dash in dasharrayStr) {
                                        newShape.strokeDashArray.add(dash.toDouble())
                                    }
                                }
                                model.addShape(newShape)
                            }
                            "Rectangle" -> {
                                val newShape = Rectangle()
                                newShape.x = loadFileReader.nextLine().toDouble()
                                newShape.y = loadFileReader.nextLine().toDouble()
                                newShape.width = loadFileReader.nextLine().toDouble()
                                newShape.height = loadFileReader.nextLine().toDouble()
                                newShape.fill = Color.web(loadFileReader.nextLine())
                                newShape.stroke = Color.web(loadFileReader.nextLine())
                                newShape.strokeWidth = loadFileReader.nextLine().toDouble()
                                var fileDashArray = loadFileReader.nextLine()
                                fileDashArray =
                                    fileDashArray.replace("[", "").replace("]", "").replace("\\s".toRegex(), "")
                                if (fileDashArray != "") {
                                    var dasharrayStr = fileDashArray.split(",")
                                    for (dash in dasharrayStr) {
                                        newShape.strokeDashArray.add(dash.toDouble())
                                    }
                                }
                                model.addShape(newShape)
                            }
                        }
                    }
                    model.needsToLoad()
                    model.saved()
                }
            }
        }
        helpAbout.setOnAction {
            val aboutMe = Dialog<String>()
            aboutMe.title = "About Me"
            val closeButton = ButtonType("Ok", ButtonBar.ButtonData.OK_DONE)
            aboutMe.contentText = "SketchIt\nPublished by: Evelyn Law\nSerial Number: e6law"
            aboutMe.dialogPane.buttonTypes.add(closeButton)
            aboutMe.showAndWait()
        }
        this.menus.addAll(fileMenu, editMenu, helpMenu)
        model.addView(this)
    }
}