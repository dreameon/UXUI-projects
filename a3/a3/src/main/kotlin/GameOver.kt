import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.io.FileInputStream
import Model.SCENES
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment

internal class GameOver(
    private val model: Model
) : VBox(), IView {
    val gameFont = Font.loadFont(FileInputStream("${System.getProperty("user.dir")}/src/main/resources/fonts/PressStart2P-Regular.ttf"),16.0)
    val titleFont = Font.loadFont(FileInputStream("${System.getProperty("user.dir")}/src/main/resources/fonts/PressStart2P-Regular.ttf"),36.0)
    // Title/Menu Scene
    private val title = ImageView("images/logo.png")
    val spacer1 = Region()
    val spacer2 = Region()
    private val gameOver = Text("GAME OVER")
    val instructions = Text("ENTER - Restart Game\n\nQ - Quit Game\n\n")

    override fun updateView() {
    }
    init{
        val scene = Scene(this, 1600.0, 1000.0)
        model.createScene(4,scene)
        spacer1.prefHeight = 100.0
        spacer2.prefHeight = 100.0
        gameOver.font = titleFont
        instructions.font = gameFont
        alignment = Pos.CENTER
        padding = Insets(50.0, 50.0, 0.0, 50.0)
        children.addAll(title, spacer1, gameOver, spacer2, instructions)
        scene.addEventFilter(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
            println("A key has been pressed")
            when (event.code) {
                KeyCode.ENTER -> {
                    val level1 = Lvl1(model)
                    model.newGame()
                    model.setScene(SCENES.GAMESCENE1)
                }
                KeyCode.Q -> {
                    Platform.exit()
                }
                else -> {}
            }
        }
        model.addView(this)
    }
}