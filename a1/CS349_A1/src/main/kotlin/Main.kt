import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.io.File

class Main : Application() {
    override fun start(stage: Stage) {

        // top section: toolbar
        val toolBar = ToolBar(
            Button("Home"),
            Button("Previous"),
            Button("Next")
        )
        // determine starting directory
        // this will be the "test" subfolder in your project directory
        val dir = File("${System.getProperty("user.dir")}/test/")

        // side: tree view
        val folderIcon: Node = ImageView(Image(javaClass.getResourceAsStream("folder.png")))
        val rootItem = TreeItem<Any?>(dir.walk().maxDepth(1).elementAt(0).name, folderIcon)
        rootItem.isExpanded = true

        // populate the side menu with contents in directory
        for (content in dir.walk().maxDepth(1).drop(1)) {
            var item = TreeItem<Any?>(content.name)
            if (content.isDirectory()) {
                item = TreeItem<Any?>(content.name +"/")
            }

            // handle events

            rootItem.children.add(item)
        }
        val tree = TreeView<Any?>(rootItem)


        // center: preview
        val text = TextArea()
        text.isWrapText = true
        var str = "IDK WHAT IM DOING"
        text.text = str
        val center = HBox(text)
        // center.setOnMouseMoved { println(it.x.toString() + "," + it.y)}

        // bottom: status bar
        val label = Label("Status information can be placed here")
        val checkmark: Node = ImageView(Image(javaClass.getResourceAsStream("check.png")))
        val status = HBox(checkmark, label)

        // setup the scene
        val border = BorderPane()
        border.top = toolBar
        border.left = tree
        border.center = center
        border.bottom = status
        val scene = Scene(border)

        // setup and show the window
        stage.title = "File Explorer"
        stage.isResizable = true

        stage.width = 640.0
        stage.minWidth = 512.0
        stage.maxWidth = 768.0

        stage.height = 480.0
        stage.minHeight = 384.0
        stage.maxHeight = 576.0

        stage.scene = scene
        stage.show()
    }
}