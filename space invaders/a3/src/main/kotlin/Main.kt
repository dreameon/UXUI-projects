import javafx.application.Application
import javafx.stage.Stage

class Main  : Application() {
    // really no point on making a class - just here for the stage and creating the model :D
    override fun start(stage: Stage?) {
        stage!!.title = "Space Invaders"
        // create the model and feed the stage to it
        val model = Model()
        val menu = TitleScreen(model)
        val gameOver = GameOver(model)
        val gameWon = GameWon(model)
        model.createStage(stage)
        stage.show()
    }
}