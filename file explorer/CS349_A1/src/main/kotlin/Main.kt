import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.*
import java.nio.file.DirectoryNotEmptyException
import java.util.*
import kotlin.io.path.moveTo

class Main : Application() {
    var hidden = true
    // helper function that returns an observable list of all the children of a given root file
    fun populate(root: File) : ObservableList<File> {
        val contents = FXCollections.observableArrayList<File>()
        // populate the side menu with contents in directory
        for (content in root.listFiles()!!) {
            val file = content.relativeTo(root)
            if (!hidden || !file.name.startsWith(".")){ contents.add(file) }
        }
        return contents
    }
    fun fileViewRefresh(root: File, fileView: ListView<File>){
        fileView.items.clear()
        fileView.items.addAll(populate(root))
        for (item in fileView.items)  {
            if (item.isDirectory) {

            }
            else if (item.extension == "png" || item.extension == "jpg" || item.extension == "bmp") {

            }
        }
    }
    fun renameDialogue(parent: File, fileView: ListView<File>){
        val selected = fileView.selectionModel.selectedItem
        selected ?.let {
            val promptWindow = Stage()
            promptWindow.initStyle(StageStyle.UTILITY)
            promptWindow.title = "Rename File"
            val textBox = VBox()
            val promptScene = Scene(textBox)
            val prompt = Label("Rename $selected as: ")
            val rename = TextField()
            textBox.padding = Insets(5.0,5.0,10.0,5.0)
            val submitButton = Button("Submit")
            rename.promptText = "Please enter the new name of the file"
            textBox.children.addAll(prompt, rename, submitButton)
            val renameKey: EventHandler<KeyEvent> = EventHandler {
                if (it.code == KeyCode.ENTER) {
                    val input = rename.text
                    try {
                        parent.resolve(selected).toPath().moveTo(parent.resolve(selected).resolveSibling(input+"." + selected.extension).toPath())
                        fileViewRefresh(parent, fileView)
                        promptWindow.close()
                    }
                    catch (e: FileAlreadyExistsException) {
                        val alert = Alert(Alert.AlertType.ERROR,"File name already exists.")
                        alert.show()
                        promptWindow.close()
                    }
                    catch (e: DirectoryNotEmptyException) {
                        val alert = Alert(Alert.AlertType.ERROR,"Directory is not empty")
                        alert.show()
                        promptWindow.close()
                    }
                    catch (e: Exception) {
                        val alert = Alert(Alert.AlertType.ERROR,"Cannot rename $selected to ${input + "." + selected.extension}.")
                        alert.show()
                        promptWindow.close()
                    }
                }
            }
            promptWindow.addEventFilter(KeyEvent.KEY_PRESSED,renameKey)
            submitButton.setOnAction {
                val input = rename.text
                try {
                   parent.resolve(selected).toPath().moveTo(parent.resolve(selected).resolveSibling(input+"." + selected.extension).toPath())
                    fileViewRefresh(parent, fileView)
                    promptWindow.close()
                }
                catch (e: FileAlreadyExistsException) {
                    val alert = Alert(Alert.AlertType.ERROR,"File name already exists.")
                    alert.show()
                    promptWindow.close()
                }
                catch (e: DirectoryNotEmptyException) {
                    val alert = Alert(Alert.AlertType.ERROR,"Directory is not empty")
                    alert.show()
                    promptWindow.close()
                }
                catch (e: Exception) {
                    val alert = Alert(Alert.AlertType.ERROR,"Cannot rename $selected to ${input + "." + selected.extension}.")
                    alert.show()
                    promptWindow.close()
                }
            }

            promptWindow.width = 250.0
            promptWindow.minWidth = 200.0
            promptWindow.maxWidth = 400.0
            promptWindow.isResizable = false
            promptWindow.minHeight = 120.0
            promptWindow.maxHeight = 120.0

            promptWindow.scene = promptScene
            promptWindow.show()
        }
    }
    fun moveDialogue(parent: File, fileView: ListView<File>){

        val selected = fileView.selectionModel.selectedItem
        selected?.let {
            val promptWindow = Stage()
            promptWindow.initStyle(StageStyle.UTILITY)
            promptWindow.title = "Move File"
            val textBox = VBox()
            val promptScene = Scene(textBox)
            val prompt = Label("Move $selected to: ")
            val move = TextField()
            textBox.padding = Insets(5.0, 5.0, 10.0, 5.0)
            val submitButton = Button("Submit")
            move.promptText = "Please enter file path to new directory"
            textBox.children.addAll(prompt, move, submitButton)
            val moveKey: EventHandler<KeyEvent> = EventHandler {
                // evaluate which key was pressed
                if (it.code == KeyCode.ENTER) {
                    val input = move.text
                    try {
                        parent.resolve(selected).toPath().moveTo(parent.resolve(input).resolve(selected).toPath().toAbsolutePath())
                        fileViewRefresh(parent, fileView)
                        promptWindow.close()
                    } catch (e: FileAlreadyExistsException) {
                        val alert = Alert(Alert.AlertType.ERROR, "File name already exists.")
                        alert.show()
                        promptWindow.close()
                    } catch (e: DirectoryNotEmptyException) {
                        val alert = Alert(Alert.AlertType.ERROR, "Directory is not empty")
                        alert.show()
                        promptWindow.close()
                    } catch (e: Exception) {
                        val alert = Alert(Alert.AlertType.ERROR, "Cannot move $selected to $input.")
                        alert.show()
                        promptWindow.close()
                    }
                }
            }
            promptWindow.addEventFilter(KeyEvent.KEY_PRESSED,moveKey)
            submitButton.setOnAction {
                val input = move.text
                try {
                    parent.resolve(selected).toPath().moveTo(parent.resolve(input).resolve(selected).toPath().toAbsolutePath())
                    fileViewRefresh(parent, fileView)
                    promptWindow.close()
                } catch (e: FileAlreadyExistsException) {
                    val alert = Alert(Alert.AlertType.ERROR, "File already exists.")
                    alert.showAndWait()
                } catch (e: DirectoryNotEmptyException) {
                    val alert = Alert(Alert.AlertType.ERROR, "Directory is not empty")
                    alert.showAndWait()
                } catch (e: Exception) {
                    val alert = Alert(Alert.AlertType.ERROR, "Cannot move $selected to ${parent.resolve(input).toPath().toAbsolutePath()}.")
                    alert.showAndWait()
                }
            }

            promptWindow.width = 250.0
            promptWindow.minWidth = 200.0
            promptWindow.maxWidth = 400.0

            promptWindow.minHeight = 120.0
            promptWindow.maxHeight = 120.0
            promptWindow.isResizable = false
            promptWindow.scene = promptScene
            promptWindow.show()
        }
    }
    fun deleteRecurse(parent: File, selected: File){
        if (parent.resolve(selected).isDirectory) {
            for (content in parent.resolve(selected).listFiles()!!) {
                deleteRecurse(parent.resolve(selected), content)
            }
        }
        parent.resolve(selected).delete()
    }
    fun deleteDialogue(parent: File, fileView: ListView<File>){
        val selected = fileView.selectionModel.selectedItem
        selected ?.let {
            val prompt = Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete $selected?")
            prompt.headerText = "Delete"
            prompt.title = "Delete"
            val result = prompt.showAndWait()
            if (result.isPresent && result.get() == ButtonType.OK){
                deleteRecurse(parent, selected)
                fileViewRefresh(parent, fileView)
            }
        }
    }
    fun preview(parent: File, file: File, label: Label, center: StackPane) {
        val newFilePath = parent.resolve(file)
        label.text = "$newFilePath"
        if(!File("$newFilePath").canRead()){
            val string="File cannot be read"
            val textNode = Text(string)
            val textFlow = TextFlow(textNode)
            val scrollPane = ScrollPane(textFlow)
            center.children.setAll(scrollPane)
        }
        else if(File("$newFilePath").isDirectory) {
            center.children.clear()
        }
        else {
            when (file.extension) {
                "png", "jpg", "bmp" -> {
                    val imageStream = FileInputStream("$newFilePath")
                    val image = ImageView(Image(imageStream))
                    imageStream.close()
                    image.isPreserveRatio = true
                    val imageView = Group(image)
                    center.children.setAll(imageView)
                    image.fitWidthProperty().bind(center.widthProperty())
                    image.fitHeightProperty().bind(center.heightProperty())

                }
                "txt", "md" -> {
                    val scanner = Scanner(newFilePath)
                    var string = ""
                    while (scanner.hasNext()) {
                        string += scanner.nextLine()
                        string += "\n"
                    }
                    scanner.close()
                    val textNode = Text(string)
                    val textFlow = TextFlow(textNode)
                    val scrollPane = ScrollPane(textFlow)
                    scrollPane.isFitToWidth = true
                    center.children.setAll(scrollPane)
                }
                else -> {
                    val string="Unsupported type"
                    val textNode = Text(string)
                    val textFlow = TextFlow(textNode)
                    val scrollPane = ScrollPane(textFlow)
                    center.children.setAll(scrollPane)
                }
            }
        }
    }
    override fun start(stage: Stage) {
        // determine starting directory
        // this will be the "test" subfolder in your project directory
        val dir = File("${System.getProperty("user.dir")}/test/")

        // ************* CREATE VIEW *************
        // top section: menubar & toolbar
        val toolBar = ToolBar()
        val homeButton = Button("Home")
        val prevButton = Button("Previous")
        val nextButton = Button("Next")
        val delButton = Button("Delete")
        val renameButton = Button("Rename")
        val moveButton = Button("Move")
        homeButton.graphic = ImageView("home.png")
        prevButton.graphic = ImageView("previous.png")
        nextButton.graphic = ImageView("next.png")
        delButton.graphic = ImageView("delete.png")
        renameButton.graphic = ImageView("rename.png")
        moveButton.graphic = ImageView("move.png")

        toolBar.items.addAll(homeButton, prevButton, nextButton, moveButton, delButton, renameButton)

        val menuBar = MenuBar()
        val fileMenu = Menu("File")
        val viewMenu = Menu("View")
        val actionsMenu = Menu("Actions")
        val optionsMenu = Menu("Options")

//        val fileNew = MenuItem("New")
//        val fileOpen = MenuItem("Open")
//        val fileClose = MenuItem("Close")
        val fileQuit = MenuItem("Quit")
        fileQuit.graphic = ImageView("quit.png")
        fileMenu.items.addAll(fileQuit)

        val viewHome = MenuItem("Home")
        val viewPrev = MenuItem("Previous")
        val viewNext = MenuItem("Next")
        viewHome.graphic = ImageView("home_small.png")
        viewPrev.graphic = ImageView("previous_small.png")
        viewNext.graphic = ImageView("next_small.png")
        viewMenu.items.addAll(viewHome, viewPrev, viewNext)

        val actionMove = MenuItem("Move")
        val actionDel = MenuItem("Delete")
        val actionRename = MenuItem("Rename")
        actionRename.graphic = ImageView("rename_small.png")
        actionMove.graphic = ImageView("move_small.png")
        actionDel.graphic = ImageView("delete_small.png")
        actionsMenu.items.addAll(actionMove, actionDel, actionRename)

        val optionShow = RadioMenuItem("Show Hidden Files")
        optionsMenu.items.addAll(optionShow)

        menuBar.menus.addAll(fileMenu, viewMenu, actionsMenu, optionsMenu)

        // stack menu and toolbar in the top region
        val topVBox = VBox(menuBar, toolBar)

        // side: file list view
        var parent = dir
        val fileList = populate(parent)
        val fileView = ListView(fileList)

        // center: preview
        val center = StackPane()

        // bottom: status bar
        val label = Label()
        val checkmark: Node = ImageView(Image(javaClass.getResourceAsStream("check.png")))
        val status = HBox(checkmark, label)

        // ************* HANDLE EVENTS *************
        // fileView events
        val fileClick: EventHandler<MouseEvent> = EventHandler { event ->
            val selected = fileView.selectionModel.selectedItem
            selected ?. let{
                // edit the label to reflect selected file
                preview(parent, selected, label, center)
                // if user double-clicked on directory, repopulate the listView with the new file list
                if ((parent.resolve(selected).isDirectory) && (event.clickCount == 2)) {
                    parent = parent.resolve(selected)
                    fileViewRefresh(parent, fileView)
                }
            }
        }
        val fileKey: EventHandler<KeyEvent> = EventHandler {
            val selected = fileView.selectionModel.selectedItem
            // evaluate which key was pressed
            when (it.code) {
                KeyCode.ENTER -> {
                    selected ?.let {
                        if (parent.resolve(selected).isDirectory) {
                            parent = parent.resolve(selected)
                            fileViewRefresh(parent, fileView)
                        }
                    }
                }
                KeyCode.BACK_SPACE, KeyCode.DELETE -> {
                    if (parent != dir) {
                        parent = parent.parentFile
                        fileViewRefresh(parent, fileView)
                    }
                }
                KeyCode.UP, KeyCode.DOWN -> {
                    fileView.selectionModel.selectedItemProperty().addListener {
                            _: ObservableValue<out File>, _, newFile ->
                        newFile  ?.let {
                            preview(parent, newFile, label, center)
                        }
                    }
                }
            }
        }

        fileView.addEventFilter(MouseEvent.MOUSE_CLICKED,fileClick)
        fileView.addEventFilter(KeyEvent.KEY_PRESSED,fileKey)

        // toolbar events
        homeButton.setOnAction {
            parent = dir
            fileViewRefresh(parent, fileView)
        }
        prevButton.setOnAction {
            if (parent != dir) {
                parent = parent.parentFile
                fileViewRefresh(parent, fileView)
            }
        }
        nextButton.setOnAction {
            val selected = fileView.selectionModel.selectedItem
            selected ?.let {
                if (parent.resolve(selected).isDirectory) {
                    parent = parent.resolve(selected)
                    fileViewRefresh(parent, fileView)
                }
            }
        }
        delButton.setOnAction { deleteDialogue(parent, fileView) }
        renameButton.setOnAction { renameDialogue(parent, fileView) }
        moveButton.setOnAction { moveDialogue(parent, fileView) }

        // fileMenu events
        fileQuit.setOnAction { Platform.exit() }

        // viewMenu events
        viewHome.setOnAction {
            parent = dir
            fileViewRefresh(parent, fileView)
        }
        viewPrev.setOnAction {
            if (parent != dir) {
                parent = parent.parentFile
                fileViewRefresh(parent, fileView)
            }
        }
        viewNext.setOnAction {
            val selected = fileView.selectionModel.selectedItem
            selected ?.let {
                if (parent.resolve(selected).isDirectory) {
                    parent = parent.resolve(selected)
                    fileViewRefresh(parent, fileView)
                }
            }
        }

        // actionsMenu events
        actionRename.setOnAction { renameDialogue(parent, fileView) }
        actionMove.setOnAction { moveDialogue(parent, fileView) }
        actionDel.setOnAction { deleteDialogue(parent, fileView) }

        // optionsMenu events
        optionShow.setOnAction {
            hidden = !hidden
            fileViewRefresh(parent, fileView)
        }

        // setup the scene
        val border = BorderPane()
        border.top = topVBox
        border.left = fileView
        border.center = center
        border.bottom = status
        val scene = Scene(border)

        // setup and show the window
        stage.title = "File Browser"
        stage.isResizable = false
        stage.icons.add(Image("folder.png"))
        stage.width = 640.0
       //stage.minWidth = 512.0
       // stage.maxWidth = 768.0

        stage.height = 480.0
        //stage.minHeight = 384.0
        //stage.maxHeight = 576.0

        stage.scene = scene
        stage.show()
    }
}