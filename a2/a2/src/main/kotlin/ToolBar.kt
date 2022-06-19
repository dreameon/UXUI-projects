import javafx.geometry.Insets
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.geometry.Pos

// Displays the tool selection
internal class ToolBar(
    private val model: Model
) : HBox(), IView {

    private val select = ToggleButton()
    private val erase = ToggleButton()
    private val line = ToggleButton()
    private val circle = ToggleButton()
    private val rect = ToggleButton()
    private val fill = ToggleButton()

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        when (model.selectedTool) {
            "Select" -> select.isSelected = true
            "Erase" -> erase.isSelected = true
            "Line" -> line.isSelected = true
            "Circle" -> circle.isSelected = true
            "Rect" -> rect.isSelected = true
            "Fill" -> fill.isSelected = true
        }
    }

    init {
        val col1 = VBox()
        val col2 = VBox()
        // setup the view (i.e. group+widget)
        val toggle = ToggleGroup()
        with(select) {
            graphic = ImageView("select.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            styleClass.add(".toggle-button")
            setOnAction {
                model.changeSelectedTool("Select")
            }
        }
        with(erase) {
            graphic = ImageView("erase.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            setOnAction {
                model.changeSelectedTool("Erase")
                model.changeSelectedShape(null)
            }
        }
        with(line) {
            graphic = ImageView("line.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            setOnAction {
                model.changeSelectedTool("Line")
                model.changeSelectedShape(null)
            }
        }
        with(circle) {
            graphic = ImageView("circle.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            setOnAction {
                model.changeSelectedTool("Circle")
                model.changeSelectedShape(null)
            }
        }
        with(rect) {
            graphic = ImageView("rectangle.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            setOnAction {
                model.changeSelectedTool("Rect")
                model.changeSelectedShape(null)
            }
        }
        with(fill) {
            graphic = ImageView("fill.png")
            toggleGroup = toggle
            padding= Insets(10.0,10.0,10.0,10.0)
            setOnAction {
                model.changeSelectedTool("Fill")
                model.changeSelectedShape(null)
            }
        }

        // add buttons to the selection view

        col1.children.addAll(select,line,rect)
        col2.children.addAll(erase,circle,fill)
        alignment = Pos.CENTER
        children.addAll(col1,col2)

        for (tool in col1.children + col2.children) {
            with(tool){
                styleClass.add(".toggle-button")
            }
        }

        // register with the model when we're ready to start receiving data
        stylesheets.add("Stylesheet.css")
        model.addView(this)
    }
}