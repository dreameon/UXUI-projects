import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.layout.VBox.setVgrow
import javafx.stage.Stage

// MVC model taken from in-class MVC2 example
// MVC with coupled View and Controller (a more typical method than MVC1)
// A simple MVC example inspired by Joseph Mack, http://www.austintek.com/mvc/
// This version uses MVC: two views coordinated with the observer pattern, but no separate controller.
class MVC : Application() {

    override fun start(stage: Stage) {
        // window name
        stage.title = "SketchIt"

        // create and initialize the Model to hold our counter
        val model = Model()

        // create each view, and tell them about the model
        // the views will register themselves with the model
        val view1 = ToolBar(model)
        val view2 = StyleBar(model)
        val tools = VBox()
        setVgrow(view1, Priority.ALWAYS)
        setVgrow(view2, Priority.ALWAYS)
        tools.children.add(view1) // top-view
        tools.children.add(view2) // bottom-view
        tools.spacing = 50.0
        val toolBar = ScrollPane(tools)

        // creat top view (menu bar)
        val menuBar = Menu(model)

        // Use the graphics context to draw on a canvas
        val view3 = Canvas(model)

        val borderPane = BorderPane()
        borderPane.left = toolBar
        toolBar.padding = Insets(0.0,5.0,0.0,5.0)
        borderPane.center = view3
        borderPane.top = menuBar
        borderPane.prefWidth = 900.0
        borderPane.prefHeight = 500.0

        // Add grid to a scene (and the scene to the stage)
        val scene = Scene(borderPane)
        stage.isResizable = true
        stage.scene = scene
        stage.width = 1024.0
        stage.height = 768.0
        stage.minWidth = 640.0
        stage.minHeight = 480.0
        stage.maxWidth = 1920.0
        stage.maxHeight = 1080.0
        stage.icons.add(Image("stageIcon.png"))
        stage.show()
    }
}