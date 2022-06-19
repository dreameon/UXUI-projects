import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.Shape

// Model stores the selected tool, line thickness/style, fill and line colour,
// and selected shape

class Model {

    //region View Management

    // all views of this model
    private val views: ArrayList<IView> = ArrayList()

    // method that the views can use to register themselves with the Model
    // once added, they are told to update and get state from the Model
    fun addView(view: IView) {
        views.add(view)
        view.updateView()
    }

    // the model uses this method to notify all of the Views that the data has changed
    // the expectation is that the Views will refresh themselves to display new data when appropriate
    private fun notifyObservers() {
        print(selectedShape)
        for (view in views) {
            view.updateView()
        }
    }

    //endregion

    // attributes that are of our concern in the model
    var selectedTool = ""
    var lineColour: Color = Color.BLACK
    var fillColour: Color = Color.BLACK
    var lineThickness = 1.0
    var lineStyle = ArrayList<Double>()
    var selectedShape: Shape? = null
    var shapeGroup = Group()
    var copiedShape: Shape? = null
    // booleans to let us know if a recent action has been taken and a future action needs to be taken
    var paste = false
    var load = false
    var save = false


    // method that the Controller uses to tell the Model to change state
    // in a larger application there would probably be multiple entry points like this
    fun changeSelectedTool(tool: String) {
        println("Model: change selected tool to $tool")
        selectedTool = tool
        notifyObservers()
    }
    fun changeSelectedShape(newShape: Shape?) {
        println("Model: change shape to $newShape")
        if (newShape == null) selectedShape?.id = "#unselected"
        selectedShape = newShape
        notifyObservers()
    }
    fun changeLineColour(color: Color) {
        println("Model: change line colour to $color")
        lineColour = color
        save = true
        notifyObservers()
    }
    fun changeFillColour(color: Color) {
        println("Model: change fill colour to $color")
        fillColour = color
        save = true
        notifyObservers()
    }
    fun changeLineThickness(thickness: Double) {
        println("Model: change line thickness to $thickness")
        lineThickness = thickness
        save = true
        notifyObservers()
    }
    fun changeLineStyle(dashArray: ArrayList<Double>) {
        println("Model: change line style to $dashArray")
        lineStyle.clear()
        lineStyle = dashArray
        save = true
        notifyObservers()
    }
    // for if more than one thing has changed (i.e., when a pre-existing shape has been selected)
    fun modifyShape(newShape: Shape, stroke: Color, fill: Color?, thickness: Double, dashArray: ArrayList<Double>) {
        selectedShape = newShape
        lineColour = stroke
        if (fill != null) {
            fillColour = fill
        }
        lineThickness = thickness
        lineStyle.clear()
        lineStyle = dashArray
        save = true
        notifyObservers()
    }

    // add a shape to our shapeGroup
    fun addShape(shape: Shape) {
        shapeGroup.children.add(shape)
        save = true
        notifyObservers()
    }

    // remove a shape from our shapeGroup
    fun removeShape(shape: Shape) {
        shapeGroup.children.remove(shape)
        save = true
        if (shapeGroup.children.isEmpty()) {
            save = false
        }
        notifyObservers()
    }

    fun clearCanvas() {
        shapeGroup.children.clear()
        selectedShape = null
        selectedTool = "Select"
        save = false
        notifyObservers()
    }

    // set copied shape for later use (we're going to make a copy of this)
    fun copyShape(shape: Shape) {
        copiedShape = shape
        println("copied shape: $shape")
        notifyObservers()
    }

    fun cutShape(shape: Shape) {
        copiedShape = shape
        shapeGroup.children.remove(shape)
        println("cut shape: $shape")
        save = true
        notifyObservers()
    }

    // let observers know that a new shape was pasted
    fun pasteShape(shape: Shape) {
        paste = true
        save = true
        notifyObservers()
    }

    fun pasted() {
        paste = false
        notifyObservers()
    }

    fun needsToLoad() {
        load = true
        notifyObservers()
    }

    fun loaded() {
        load = false
        notifyObservers()
    }

    fun saved() {
        save = false
        notifyObservers()
    }
}