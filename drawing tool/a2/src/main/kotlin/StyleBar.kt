import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.shape.*

// Displays the line and fill colour as well as the line thickness/style
internal class StyleBar(
    private val model: Model
) : VBox(), IView {
    private var lineColPreview = ColorPicker()
    private var fillColPreview = ColorPicker()
    private val lineWidthPreview = HBox()
    private val lineStylePreview = HBox()
    private val thick1 = RadioButton()
    private val thick2 = RadioButton()
    private val thick3 = RadioButton()
    private val thick4 = RadioButton()
    private val dash1 = RadioButton()
    private val dash2 = RadioButton()
    private val dash3 = RadioButton()
    private val dash4 = RadioButton()

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        // get the line colour, fill colour, line thickness and line style
       fillColPreview.value = model.fillColour
       lineColPreview.value = model.lineColour
        when (model.lineThickness) {
            1.0 -> thick1.isSelected = true
            3.0 -> thick2.isSelected = true
            6.0 -> thick3.isSelected = true
            10.0 -> thick4.isSelected = true
        }
        when (model.lineStyle.size) {
            0 -> dash1.isSelected = true
            1 -> dash2.isSelected = true
            2 -> dash3.isSelected = true
            4 -> dash4.isSelected = true
        }
        when (model.selectedTool) {
            "Select" -> {
                fillColPreview.isDisable = true
                lineColPreview.isDisable = true
                lineWidthPreview.isDisable = true
                lineStylePreview.isDisable = true
                when (model.selectedShape) {
                    is Rectangle -> {
                        fillColPreview.isDisable = false
                        lineColPreview.isDisable = false
                        lineWidthPreview.isDisable = false
                        lineStylePreview.isDisable = false
                    }
                    is Circle -> {
                        fillColPreview.isDisable = false
                        lineColPreview.isDisable = false
                        lineWidthPreview.isDisable = false
                        lineStylePreview.isDisable = false
                    }
                    is Line -> {
                        fillColPreview.isDisable = true
                        lineColPreview.isDisable = false
                        lineWidthPreview.isDisable = false
                        lineStylePreview.isDisable = false
                    }
                }
            }
            "Erase" -> {
                fillColPreview.isDisable = true
                lineColPreview.isDisable = true
                lineWidthPreview.isDisable = true
                lineStylePreview.isDisable = true
            }
            "Line" -> {
                fillColPreview.isDisable = true
                lineColPreview.isDisable = false
                lineWidthPreview.isDisable = false
                lineStylePreview.isDisable = false
            }
            "Circle" -> {
                fillColPreview.isDisable = false
                lineColPreview.isDisable = false
                lineWidthPreview.isDisable = false
                lineStylePreview.isDisable = false
            }
            "Rect" -> {
                fillColPreview.isDisable = false
                lineColPreview.isDisable = false
                lineWidthPreview.isDisable = false
                lineStylePreview.isDisable = false
            }
            "Fill" -> {
                fillColPreview.isDisable = false
                lineColPreview.isDisable = true
                lineWidthPreview.isDisable = true
                lineStylePreview.isDisable = true
            }
            "" -> {
                fillColPreview.isDisable = true
                lineColPreview.isDisable = true
                lineWidthPreview.isDisable = true
                lineStylePreview.isDisable = true
            }
        }
    }

    init {
        val colorPreview = HBox()

        // set color pickers for the line and fill previews
        with (lineColPreview) {
            styleClass.add("button")
            //style =("-fx-border-color: $lineCol; -fx-border-width: 2px;" +
                   // "-fx-background-color: transparent")
            prefWidth = 55.0
            prefHeight = 25.0
            setOnAction { model.changeLineColour(value) }
        }
        with (fillColPreview) {
            styleClass.add("button")
            prefWidth = 55.0
            prefHeight = 25.0
            setOnAction { model.changeFillColour(value) }
        }


        // set up line width and line style previews
        val thickToggle = ToggleGroup()
        with (thick1){
            graphic = ImageView("thick1.png")
            toggleGroup = thickToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            isSelected = true
            setOnAction { model.changeLineThickness(1.0) }
        }
        with (thick2){
            graphic = ImageView("thick2.png")
            toggleGroup = thickToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineThickness(3.0) }
        }
        with (thick3){
            graphic = ImageView("thick3.png")
            toggleGroup = thickToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineThickness(6.0) }
        }
        with (thick4){
            graphic = ImageView("thick4.png")
            toggleGroup = thickToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineThickness(10.0) }
        }
        val dashToggle = ToggleGroup()
        with (dash1){
            graphic = ImageView("dash1.png")
            toggleGroup = dashToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            isSelected = true
            setOnAction { model.changeLineStyle(arrayListOf()) }
        }
        with (dash2){
            graphic = ImageView("dash2.png")
            toggleGroup = dashToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineStyle(arrayListOf(2.0)) }
        }
        with (dash3){
            graphic = ImageView("dash3.png")
            toggleGroup = dashToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineStyle(arrayListOf(10.0,2.0)) }
        }
        with (dash4){
            graphic = ImageView("dash4.png")
            toggleGroup = dashToggle
            styleClass.remove("radio-button");
            styleClass.add("toggle-button");
            padding= Insets(5.0,2.5,5.0,2.5)
            setOnAction { model.changeLineStyle(arrayListOf(5.0, 4.0, 1.0, 4.0)) }
        }

        // add options to respective widgets
        lineWidthPreview.children.addAll(thick1, thick2, thick3, thick4)
        lineStylePreview.children.addAll(dash1, dash2, dash3, dash4)
        colorPreview.children.addAll(lineColPreview, fillColPreview)

        lineWidthPreview.alignment = Pos.CENTER
        lineStylePreview.alignment = Pos.CENTER
        colorPreview.alignment = Pos.CENTER

        // stylesheet used for toggle and radio buttons
        stylesheets.add("Stylesheet.css")

        // add widgets to the pane
        spacing = 5.0
        children.addAll(colorPreview,lineWidthPreview, lineStylePreview)

        // register with the model when we're ready to start receiving data
        model.addView(this)
    }
}