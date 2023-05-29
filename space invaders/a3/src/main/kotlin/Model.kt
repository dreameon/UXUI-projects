import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.stage.Stage

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
        for (view in views) {
            view.updateView()
        }
    }

    //endregion

    // attributes that are of our concern in the model
    enum class SCENES {
        MENUSCENE, GAMESCENE1, GAMESCENE2, GAMESCENE3, GAMEOVERSCENE, CONGRATSSCENE
    }
    var score = 0
    var lives = 3
    var level = 1
    var stage: Stage? = null
    private var gameScene1: Scene? = null
    private var gameScene2: Scene? = null
    private var gameScene3: Scene? = null
    private var menuScene: Scene? = null
    private var gameOverScene: Scene? = null
    private var congratsScene: Scene? = null

    // method that the Controller uses to tell the Model to change state
    // in a larger application there would probably be multiple entry points like this
    fun createStage(myStage: Stage) {
        stage = myStage
        // show starting scene
        setScene(SCENES.MENUSCENE)
        notifyObservers()
    }

    fun createScene(sceneType: Int, myScene: Scene) {
        when (sceneType) {
            0 -> menuScene = myScene
            1 -> gameScene1 = myScene
            2 -> gameScene2 = myScene
            3 -> gameScene3 = myScene
            4 -> gameOverScene = myScene
            5 -> congratsScene = myScene
        }
    }

    fun setScene(scene: SCENES) {
        when (scene) {
            SCENES.MENUSCENE -> {
                stage?.title = "Title Screen"
                stage?.scene = menuScene
            }
            SCENES.GAMESCENE1 -> {
                stage?.title = "Level 1"
                level = 1
                stage?.scene = gameScene1
            }
            SCENES.GAMESCENE2 -> {
                stage?.title = "Level 2"
                level = 2
                stage?.scene = gameScene2
            }
            SCENES.GAMESCENE3 -> {
                stage?.title = "Level 3"
                level = 3
                stage?.scene = gameScene3
            }
            SCENES.GAMEOVERSCENE -> {
                stage?.title = "Game Over"
                stage?.scene = gameOverScene
            }
            SCENES.CONGRATSSCENE -> {
                stage?.title = "You Won!"
                stage?.scene = congratsScene
            }
        }
        notifyObservers()
    }

    fun increaseScore(points: Int) {
        score += points
        notifyObservers()
    }

    fun loseLife() {
        lives -= 1
        notifyObservers()
    }

    fun newGame() {
        score = 0
        lives = 3
        level = 1
    }
}