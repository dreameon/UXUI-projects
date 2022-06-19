import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape

// CREATION OF THE RECTANGLES AND OTHER SHAPES WAS TAKEN INSPIRATION FROM THE SHAPE DEMO IN THE CLASS REPO

// Displays the canvas for the tool
internal class Canvas(
    private val model: Model
) : VBox(), IView {
    private var tool = "Select"
    private var selectedShape: Shape? = null
    private var shapeGroup = Group()
    private var strokeThickness = 1.0
    private var strokeStyle = ArrayList<Double>()
    private var strokeColor: Color = Color.BLACK
    private var fillColor: Color = Color.BLACK
    private var copiedShape: Shape? = null
    private var needToPaste = false
    private var needsToLoad = false
    private val view = Pane()

    // closest point function taken from the ClosestPoint demo in the class repo
    private fun closestPoint(M: Point2D, P0: Point2D, P1: Point2D,
                     segmentOnly: Boolean = true): Point2D {

        val v = P1.subtract(P0) // v = P1 - P0

        // early out if line is less than 1 pixel long
        if (v.magnitude() < 1.0)
            return P0

        val u = M.subtract(P0) // u = M - P0

        // scalar of vector projection ...
        val s = u.dotProduct(v) / v.dotProduct(v)

        // for testing, useful to return point on infinite like
        if (!segmentOnly) {
            return P0.add(v.multiply(s))
        }

        // find point for constrained line segment
        if (s < 0)
            return P0
        else if (s > 1)
            return P1
        else {
            val w = v.multiply(s) // w = s * v
            return P0.add(w) // Q = P0 + w
        }
    }

    // function to add events to a particular shape (after creation, pasting, or loading)
    private fun addEvents(shape:Shape) {
        when (shape) {
            is Line -> {
                with(shape) {
                    var mouseClickDeltaX = 0.0
                    var mouseClickDeltaY = 0.0
                    this.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
                        // if we're selecting the line, update the model to reflect that
                        var mouseClickOnLine = closestPoint(Point2D(event.x, event.y), Point2D(this.startX, this.startY), Point2D(this.endX, this.endY))
                        mouseClickDeltaX = mouseClickOnLine.x - this.startX
                        mouseClickDeltaY = mouseClickOnLine.y - this.startY
                        when (tool) {
                            "Select" -> {
                                this.toFront()
                                val tempDashArr = ArrayList<Double>()
                                for (dash in strokeDashArray) {
                                    tempDashArr.add(dash / strokeWidth)
                                }
                                model.modifyShape(this,stroke as Color,null,strokeWidth,tempDashArr)
                                id = "selected"

                            }
                            "Erase" -> model.removeShape(this)
                        }
                    }
                    // if we're dragging the line, make the new coordinates reflect that
                    this.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
                        if (tool == "Select") {
                            // if we have time, make this look better (make the shape coordinates relative to the shape, not the start coordinate)
                            val prevStartPoint = Point2D(this.startX,this.startY)
                            this.startX = event.x - mouseClickDeltaX
                            this.startY = event.y - mouseClickDeltaY
                            this.endX += this.startX - prevStartPoint.x
                            this.endY += this.startY - prevStartPoint.y
                            event.consume()
                        }
                    }
                }
            }
            is Circle -> {
                with(shape) {
                    var mouseClickDeltaX = 0.0
                    var mouseClickDeltaY = 0.0
                    this.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
                        // if we're selecting the rectangle, update the model to reflect that
                        mouseClickDeltaX = event.x - this.centerX
                        mouseClickDeltaY = event.y - this.centerY
                        when (tool) {
                            "Select" -> {
                                this.toFront()
                                val tempDashArr = ArrayList<Double>()
                                for (dash in strokeDashArray) {
                                    tempDashArr.add(dash / strokeWidth)
                                }
                                model.modifyShape(this,stroke as Color,fill as Color,strokeWidth,tempDashArr)
                                id = "selected"
                            }
                            "Erase" -> model.removeShape(this)
                            "Fill" -> {
                                val tempDashArr = ArrayList<Double>()
                                for (dash in strokeDashArray) {
                                    tempDashArr.add(dash / strokeWidth)
                                }
                                model.modifyShape(this, stroke as Color, fillColor, strokeWidth, tempDashArr)
                                model.changeSelectedShape(null)
                            }
                        }
                    }
                    // if we're dragging the circle, make the new coordinates reflect that
                    this.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
                        if (tool == "Select") {
                            this.centerX = event.x - mouseClickDeltaX
                            this.centerY = event.y - mouseClickDeltaY
                            event.consume()
                        }
                    }
                }
            }
            is Rectangle -> {
                with(shape) {
                    var mouseClickDeltaX = 0.0
                    var mouseClickDeltaY = 0.0
                    this.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
                        mouseClickDeltaX = event.x - this.x
                        mouseClickDeltaY = event.y - this.y
                        // if we're selecting the rectangle, update the model to reflect that
                        when (tool) {
                            "Select" -> {
                                this.toFront()
                                val tempDashArr = ArrayList<Double>()
                                for (dash in strokeDashArray) {
                                    tempDashArr.add(dash / strokeWidth)
                                }
                                model.modifyShape(this,stroke as Color,fill as Color,strokeWidth,tempDashArr)
                                id = "selected"
                            }
                            "Erase" -> model.removeShape(this)
                            "Fill" -> {
                                val tempDashArr = ArrayList<Double>()
                                for (dash in strokeDashArray) {
                                    tempDashArr.add(dash / strokeWidth)
                                }
                                model.modifyShape(this, stroke as Color, fillColor, strokeWidth, tempDashArr)
                                model.changeSelectedShape(null)
                            }
                        }
                    }

                    // if we're dragging the rectangle, make the new coordinates reflect that
                    this.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
                        if (tool == "Select") {
                            this.x = event.x - mouseClickDeltaX
                            this.y = event.y - mouseClickDeltaY
                            event.consume()
                        }
                    }
                }
            }
        }
    }

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        // get what we need from the model
        tool = model.selectedTool
        strokeThickness = model.lineThickness
        strokeColor = model.lineColour
        fillColor = model.fillColour
        strokeStyle = model.lineStyle
        selectedShape = model.selectedShape
        shapeGroup = model.shapeGroup
        needToPaste = model.paste
        needsToLoad = model.load
        copiedShape = model.copiedShape
        // reset the view to display the new shapeGroup
        view.children.clear()
        view.children.add(shapeGroup)
        // assign these new values to the selected shape, if any
        selectedShape?.let { selectedShape ->
            selectedShape.stroke = strokeColor
            selectedShape.fill = fillColor
            selectedShape.strokeWidth = strokeThickness
            selectedShape.strokeDashArray?.clear()
            for (dash in strokeStyle) {
                selectedShape.strokeDashArray?.add(dash * selectedShape.strokeWidth)
            }
        }
        // check if there was anything we needed to load onto our canvas. If so, load and tell model
        if (needsToLoad) {
            for (shape in shapeGroup.children) {
                addEvents(shape as Shape)
            }
            model.loaded()
        }
        // check if we need to paste anything from our clipboard. If so, paste it and notify model
        if (needToPaste) {
            var newShape: Shape? = null
            when (copiedShape) {
                is Line -> {
                    newShape = Line()
                    newShape.startX = (copiedShape as Line).startX
                    newShape.startY = (copiedShape as Line).startY
                    newShape.endX = (copiedShape as Line).endX
                    newShape.endY = (copiedShape as Line).endY
                    newShape.stroke = (copiedShape as Line).stroke
                    newShape.strokeWidth = (copiedShape as Line).strokeWidth
                    newShape.strokeDashArray.clear()
                    for (dash in (copiedShape as Line).strokeDashArray) {
                        newShape.strokeDashArray.add(dash)
                    }
                    addEvents(newShape)
                }
                is Circle -> {
                    newShape = Circle()
                    newShape.centerX = (copiedShape as Circle).centerX
                    newShape.centerY = (copiedShape as Circle).centerY
                    newShape.radius = (copiedShape as Circle).radius
                    newShape.fill = (copiedShape as Circle).fill
                    newShape.stroke = (copiedShape as Circle).stroke
                    newShape.strokeWidth = (copiedShape as Circle).strokeWidth
                    newShape.strokeDashArray.clear()
                    for (dash in (copiedShape as Circle).strokeDashArray) {
                        newShape.strokeDashArray.add(dash)
                    }
                    addEvents(newShape)
                }
                is Rectangle -> {
                    newShape = Rectangle()
                    newShape.x = (copiedShape as Rectangle).x
                    newShape.y = (copiedShape as Rectangle).y
                    newShape.width = (copiedShape as Rectangle).width
                    newShape.height = (copiedShape as Rectangle).height
                    newShape.fill = (copiedShape as Rectangle).fill
                    newShape.stroke = (copiedShape as Rectangle).stroke
                    newShape.strokeWidth = (copiedShape as Rectangle).strokeWidth
                    newShape.strokeDashArray.clear()
                    for (dash in (copiedShape as Rectangle).strokeDashArray) {
                        newShape.strokeDashArray.add(dash)
                    }
                    addEvents(newShape)
                }
            }
            // don't really need the null check, but just in case
            // add the shape to the model after it has been pasted (and let others know that I've already pasted it)
            if (newShape != null) {
                model.pasted()
                model.addShape(newShape)
            }
        }
    }

    init {
        // set limits to the canvas
        view.maxHeight = 980.0
        view.maxWidth = 1840.0
        // Add the shapes to the view
        view.children.add(shapeGroup)
        // Add view to a scrollpane
        val scrollPane = ScrollPane(view)
        // Add scrollpane to the VBox and make it grow to the full size
        children.add(scrollPane)
        setVgrow(scrollPane, Priority.ALWAYS)
        var mostNegative = Point2D(0.0, 0.0)

        // User pressed on the canvas (if the event hasn't triggered any handler for the existing shapes, we're trying
        // to make a new shape here).
        addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
            when (tool) {
                "Select" -> {
                    model.changeSelectedShape(null)
                }
                "Line" -> {
                    // if we're not within the bounds, we're probably clicking on the scrollbar, so don't make a new line
                    if ((event.x < scrollPane.viewportBounds.width) && (event.y < scrollPane.viewportBounds.height)) {
                        model.changeSelectedShape(Line())
                        model.addShape(selectedShape as Line)
                        // make the new rectangle
                        with(selectedShape as Line) {
                            var newLocation = Point2D(event.x, event.y)
                            // if we're scrolling, we must determine the new coordinates relative to the view, not the scrollpane
                            if (scrollPane.vvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x,
                                    newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                                )
                            }
                            if (scrollPane.hvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                                    newLocation.y
                                )
                            }
                            startX = newLocation.x
                            startY = newLocation.y
                            endX = newLocation.x
                            endY = newLocation.y
                            // set all other properties to the current ones
                            strokeWidth = strokeThickness
                            strokeDashArray.clear()
                            for (dash in strokeStyle) {
                                strokeDashArray.add(dash * strokeWidth)
                            }
                            stroke = strokeColor
                            fill = fillColor
                        }
                    }
                }
                "Circle" -> {
                    // if we're not within the bounds, we're probably clicking on the scrollbar, so don't make a new circle
                    if ((event.x < scrollPane.viewportBounds.width) && (event.y < scrollPane.viewportBounds.height)) {
                        model.changeSelectedShape(Circle())
                        model.addShape(selectedShape as Circle)
                        // make the new rectangle
                        with(selectedShape as Circle) {
                            var newLocation = Point2D(event.x, event.y)
                            // if we're scrolling, we must determine the new coordinates relative to the view, not the scrollpane
                            if (scrollPane.vvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x,
                                    newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                                )
                            }
                            if (scrollPane.hvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                                    newLocation.y
                                )
                            }
                            centerX = newLocation.x
                            centerY = newLocation.y
                            radius = 1.0
                            // set all other properties to the current ones
                            strokeWidth = strokeThickness
                            strokeDashArray.clear()
                            for (dash in strokeStyle) {
                                strokeDashArray.add(dash * strokeWidth)
                            }
                            stroke = strokeColor
                            fill = fillColor
                        }
                    }
                }
                "Rect" -> {
                    // if we're not within the bounds, we're probably scrolling, so don't make a new rectangle
                    if ((event.x < scrollPane.viewportBounds.width) && (event.y < scrollPane.viewportBounds.height)) {
                        model.changeSelectedShape(Rectangle())
                        model.addShape(selectedShape as Rectangle)
                        // make the new rectangle
                        with(selectedShape as Rectangle) {
                            var newLocation = Point2D(event.x, event.y)
                            // if we're scrolling, we must determine the new coordinates relative to the view, not the scrollpane
                            if (scrollPane.vvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x,
                                    newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                                )
                            }
                            if (scrollPane.hvalue > 0) {
                                newLocation = Point2D(
                                    newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                                    newLocation.y
                                )
                            }
                            x = newLocation.x
                            y = newLocation.y
                            width = 1.0
                            height = 1.0
                            // set all other properties to the current ones
                            strokeWidth = strokeThickness
                            strokeDashArray.clear()
                            for (dash in strokeStyle) {
                                strokeDashArray.add(dash * strokeWidth)
                            }
                            stroke = strokeColor
                            fill = fillColor
                        }
                    }
                }
            }
        }
        // User dragged something (and it wasn't a shape) - they must be drawing !!
        addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
            when (tool) {
                "Line" -> {
                    var newLocation = Point2D(event.x, event.y)
                    // again, just to determine the relative coordinates from the view rather than the scroll pane
                    if (scrollPane.vvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x,
                            newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                        )
                    }
                    if (scrollPane.hvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                            newLocation.y
                        )
                    }
                    // set new properties of line and let the model know to update that shape
                    selectedShape?.let {
                        with(selectedShape as Line) {
                            endX = newLocation.x
                            endY = newLocation.y
                        }
                        val tempDashArr = ArrayList<Double>()
                        for (dash in it.strokeDashArray) {
                            tempDashArr.add(dash / it.strokeWidth)
                        }
                        model.modifyShape(it, it.stroke as Color, it.fill as Color, it.strokeWidth, tempDashArr)
                    }
                }
                "Circle" -> {
                    var newLocation = Point2D(event.x, event.y)
                    // again, just to determine the relative coordinates from the view rather than the scroll pane
                    if (scrollPane.vvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x,
                            newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                        )
                    }
                    if (scrollPane.hvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                            newLocation.y
                        )
                    }
                    // set new properties of line and let the model know to update that shape
                    selectedShape?.let {
                        with(selectedShape as Circle) {
                            val mousePoint = Point2D(newLocation.x, newLocation.y)
                            radius = mousePoint.distance(centerX, centerY)
                            mostNegative =
                                Point2D(scrollPane.content.boundsInLocal.minX, scrollPane.content.boundsInLocal.minY)
                        }
                        val tempDashArr = ArrayList<Double>()
                        for (dash in it.strokeDashArray) {
                            tempDashArr.add(dash / it.strokeWidth)
                        }
                        model.modifyShape(it, it.stroke as Color, it.fill as Color, it.strokeWidth, tempDashArr)
                    }
                }
                "Rect" -> {
                    var newLocation = Point2D(event.x, event.y)
                    // again, just to determine the relative coordinates from the view rather than the scroll pane
                    if (scrollPane.vvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x,
                            newLocation.y + (scrollPane.vvalue * (scrollPane.content.boundsInLocal.height + mostNegative.y - scrollPane.viewportBounds.height))
                        )
                    }
                    if (scrollPane.hvalue > 0) {
                        newLocation = Point2D(
                            newLocation.x + (scrollPane.hvalue * (scrollPane.content.boundsInLocal.width + mostNegative.x - scrollPane.viewportBounds.width)),
                            newLocation.y
                        )
                    }
                    // set new properties of line and let the model know to update that shape
                    selectedShape?.let {
                        with(selectedShape as Rectangle) {
                            width = newLocation.x - x
                            height = newLocation.y - y
                        }
                        val tempDashArr = ArrayList<Double>()
                        for (dash in it.strokeDashArray) {
                            tempDashArr.add(dash / it.strokeWidth)
                        }
                        model.modifyShape(it, it.stroke as Color, it.fill as Color, it.strokeWidth, tempDashArr)
                    }
                }
            }
        }
        addEventFilter(MouseEvent.MOUSE_RELEASED) { event ->
            when (tool) {
                "Line" -> {
                    // add events handlers to the line
                    selectedShape?.let {
                        addEvents(selectedShape!!)
                        model.changeSelectedShape(null)
                    }
                }
                "Circle" -> {
                    // add events handlers to the circle
                    selectedShape?.let {
                        addEvents(selectedShape!!)
                        model.changeSelectedShape(null)
                    }
                }
                "Rect" -> {
                    // add events handlers to the rectangle
                    selectedShape?.let {
                        addEvents(selectedShape!!)
                        model.changeSelectedShape(null)
                    }
                }
            }
        }
        // if we pressed a key while having an object selected, determine what to do with it
        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if (tool == "Select") {
                when (event.code) {
                    KeyCode.DELETE -> {
                        selectedShape?.let { model.removeShape(it) }
                    }
                    KeyCode.ESCAPE -> {
                        model.changeSelectedShape(null)
                    }
                    else -> {}
                }
            }
        }
        stylesheets.add("Stylesheet.css")
        model.addView(this)
    }
}