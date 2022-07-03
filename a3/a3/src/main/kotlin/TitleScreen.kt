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

internal class TitleScreen(
    private val model: Model
) : VBox(), IView {
    val gameFont = Font.loadFont(FileInputStream("${System.getProperty("user.dir")}/src/main/resources/fonts/PressStart2P-Regular.ttf"),16.0)
    val titleFont = Font.loadFont(FileInputStream("${System.getProperty("user.dir")}/src/main/resources/fonts/PressStart2P-Regular.ttf"),36.0)
    // Title/Menu Scene
    private val title = ImageView("images/logo.png")
    val spacer1 = Region()
    val spacer2 = Region()
    val instrTitle = Text("INSTRUCTIONS\n\n")
    val instructions = Text("ENTER - Start Game\n\nA or D - Move Ship Left or Right\n\nSPACE - Fire!\n\nQ - Quit Game\n\n1 or 2 or 3 - Start Game at a Specified Level")
    val gameDetails = Text("Implemented by Evelyn Law for CS 349, University of Waterloo, 20758964")

    override fun updateView() {
    }
    init{
        val scene = Scene(this, 1600.0, 1000.0)
        model.createScene(0,scene)
        background = Background(
            BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)
        )
        spacer1.prefHeight = 100.0
        spacer2.prefHeight = 100.0
        instrTitle.font = titleFont
        instrTitle.fill = Color.WHITE
        instructions.font = gameFont
        instructions.fill = Color.WHITE
        instructions.textAlignment = TextAlignment.CENTER
        alignment = Pos.CENTER
        padding = Insets(50.0, 50.0, 0.0, 50.0)
        children.addAll(title,spacer1,instrTitle,instructions,spacer2,gameDetails)

        scene.addEventFilter(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
            println("A key has been pressed")
            when (event.code) {
                KeyCode.ENTER -> {
                    val level1 = Lvl1(model)
                    model.setScene(SCENES.GAMESCENE1)
                }
                KeyCode.DIGIT1 -> {
                    val level1 = Lvl1(model)
                    model.setScene(SCENES.GAMESCENE1)
                }
                KeyCode.DIGIT2 -> {
                    val level2 = Lvl2(model)
                    model.setScene(SCENES.GAMESCENE2)
                }
                KeyCode.DIGIT3 -> {
                    val level3 = Lvl3(model)
                    model.setScene(SCENES.GAMESCENE3)
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