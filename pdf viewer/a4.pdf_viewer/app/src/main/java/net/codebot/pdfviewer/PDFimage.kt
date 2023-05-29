package net.codebot.pdfviewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class PDFimage  // constructor
    (context: Context?) : ImageView(context) {
    val LOGNAME = "pdf_image"

    // drawing path
    var path: Path? = null
    var paths = ArrayList<Path?>()

    // image to display
    var bitmap: Bitmap? = null
    var paint = Paint()

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(LOGNAME, "Action down")
                path = Path()
                path!!.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(LOGNAME, "Action move")
                path!!.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                Log.d(LOGNAME, "Action up")
                paths.add(path)
            }
        }
        return true
    }

    // set image as background
    fun setImage(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    fun setBrush(paint: Paint) {
        this.paint = paint
    }

    override fun onDraw(canvas: Canvas) {
        // draw background
        if (bitmap != null) {
            setImageBitmap(bitmap)
        }
        // draw lines over it
        for (path in paths) {
            with(paint){
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                strokeMiter = 10f
                color = Color.BLUE
                strokeWidth = 6.0f
                style = Paint.Style.STROKE
            }
            canvas.drawPath(path!!, paint)
        }
        super.onDraw(canvas)
    }
}