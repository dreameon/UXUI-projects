import javafx.animation.AnimationTimer
import javafx.animation.Interpolator
import javafx.animation.PauseTransition
import javafx.animation.TranslateTransition
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.VBox.setVgrow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import java.io.FileInputStream
import java.util.*
import Model.SCENES
import javafx.scene.Node
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import kotlin.math.abs
import kotlin.random.Random

internal class Lvl1(
    private val model: Model
) : Pane(), IView {
    // aesthetic stuff
    private val gameFont = Font.loadFont(
        FileInputStream("${System.getProperty("user.dir")}/src/main/resources/fonts/PressStart2P-Regular.ttf"),
        18.0
    )
    private val spacer = Region()
    private val classLoader = Thread.currentThread().contextClassLoader

    // scoreboard stuff
    private var lives = 3
    private var scoreLabel = Text()
    private var livesLabel = Text()
    private var levelLabel = Text()
    private val scoreBoard = HBox()

    // alien creepy stuff
    private var alienBulletCount = 0
    private val alienGroup = Group()
    private val greenAliens = mutableListOf<String>()
    private val blueAliens = mutableListOf<String>()
    private val purpleAliens = mutableListOf<String>()
    private val alienBullets = mutableListOf<Node>()
    private var alienMinRef = Point2D(0.0, 0.0)
    private var alienMaxRef = Point2D(0.0, 0.0)
    private var ENEMY_SPEED = 0.5
    private var ENEMY_BULLET_SPEED = 3.0
    private val enemyHeight = 41.0
    private val enemyWidth = 60.0

    // player stuff
    private var PLAYER_SPEED = 3.0
    private val PLAYER_BULLET_SPEED = 4.0
    private var playerTimerSpeed = 0.0
    private var playerLeft = false
    private var playerRight = false
    private val playerHeight = 40.0
    private val playerWidth = 100.0
    private var PKFire = false

    override fun updateView() {
        scoreLabel.text = "Score:${model.score}"
        livesLabel.text = "Lives:${model.lives}"
        levelLabel.text = "Level:${model.level}"
        with(scoreLabel) {
            font = gameFont
            fill = Color.WHITE
        }
        with(livesLabel) {
            font = gameFont
            fill = Color.WHITE
        }
        with(levelLabel) {
            font = gameFont
            fill = Color.WHITE
        }
        with(scoreBoard) {
            padding = Insets(20.0, 20.0, 20.0, 20.0)
            children.clear()
            children.addAll(scoreLabel, spacer, livesLabel, levelLabel)
            spacing = 50.0
            setHgrow(spacer, Priority.ALWAYS)
            prefWidth = 1600.0
        }
        lives = model.lives
    }

    init {
        val scene = Scene(this, 1600.0, 1000.0)
        model.createScene(1, scene)
        // set initial background of vbox
        background = Background(
            BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)
        )

        // player tings
        val player = ImageView("images/player.png")
        player.x = (scene.width - playerWidth) / 2
        player.y = scene.height - playerHeight - 10.0
        val playerTimer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (player.x + playerTimerSpeed > 10.0 && player.x + playerTimerSpeed < scene.width - 10.0 - playerWidth) {
                    player.x += playerTimerSpeed // animate parameter
                }
            }
        }
        playerTimer.start()
        // timer to prevent user from spamming
        val playerBulletSpamTimer = PauseTransition(Duration.millis(1000.0 / 2))
        // set timer finished event
        playerBulletSpamTimer.onFinished = EventHandler {
            PKFire = false
            playerBulletSpamTimer.playFromStart() // restart timer
        }
        playerBulletSpamTimer.play()

        for (i in 1..50) {
            when (i % 5) {
                1 -> {
                    val greenAlien = ImageView("images/enemy3.png")
                    greenAlien.x = 400.0 + (i / 5) * (enemyWidth + 20.0)
                    greenAlien.y = 70.0
                    greenAlien.id = "greenAlien$i"
                    greenAliens.add(greenAlien.id)
                    alienGroup.children.add(greenAlien)
                }
                in 2..3 -> {
                    val blueAlien = ImageView("images/enemy2.png")
                    blueAlien.x = 400.0 + (i - 1) / 5 * (enemyWidth + 20.0)
                    blueAlien.y = 70.0 + (i - 1) % 5 * (enemyHeight + 20.0)
                    blueAlien.id = "blueAlien$i"
                    blueAliens.add(blueAlien.id)
                    alienGroup.children.add(blueAlien)
                }
                else -> {
                    val purpleAlien = ImageView("images/enemy1.png")
                    purpleAlien.x = 400.0 + (i - 1) / 5 * (enemyWidth + 20.0)
                    purpleAlien.y = 70.0 + (i - 1) % 5 * (enemyHeight + 20.0)
                    purpleAlien.id = "purpleAlien$i"
                    purpleAliens.add(purpleAlien.id)
                    alienGroup.children.add(purpleAlien)
                }
            }
        }
        // alienMinRef is the topmost corner of the first child
        alienMinRef =
            Point2D((alienGroup.children.first() as ImageView).x, (alienGroup.children.first() as ImageView).y)
        // alienMinRef is the bottommost corner of the last child
        alienMaxRef = Point2D(
            (alienGroup.children.last() as ImageView).x + 86.0,
            (alienGroup.children.last() as ImageView).y + 41.0
        )

        // animate the aliens :D
        val alienTimer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                // if we haven't reached the end of the bounds (with a padding of 10px), keep going
                if (alienMinRef.x + ENEMY_SPEED > 10.0 && alienMaxRef.x + ENEMY_SPEED < scene.width - 10.0) {
                    alienMinRef = Point2D(alienMinRef.x + ENEMY_SPEED, alienMinRef.y)
                    alienMaxRef = Point2D(alienMaxRef.x + ENEMY_SPEED, alienMaxRef.y)
                    for (alien in alienGroup.children) {
                        (alien as ImageView).x += ENEMY_SPEED
                        if (alien.boundsInParent.intersects(player.boundsInParent)) {
                            model.loseLife()
                            MediaPlayer(Media(classLoader.getResource("sounds/explosion.wav")?.toString())).play()

                            // did we die
                            if (lives == 0) {
                                println("aliens have descended onto earth :(")
                                model.setScene(SCENES.GAMEOVERSCENE)
                                playerTimer.stop()
                                playerBulletSpamTimer.stop()
                                this.stop()
                                break
                            }
                            // we did not
                            else {
                                // find a safe place to respawn
                                var respawn = false
                                while (!respawn) {
                                    println("respawning")
                                    player.x = Random.nextDouble(10.0, scene.width - playerWidth - 10.0 )
                                    respawn = true
                                    for (alien in alienGroup.children) {
                                        if (alien.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                    for (bullet in alienBullets) {
                                        if (bullet.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // if we reach the end of the bounds (with a padding of 10px), go down a level
                else {
                    alienMinRef = Point2D(alienMinRef.x, alienMinRef.y + enemyHeight)
                    alienMaxRef = Point2D(alienMaxRef.x, alienMaxRef.y + enemyHeight)
                    ENEMY_SPEED *= -1
                    if (abs(ENEMY_SPEED) <= 2.0) {
                        MediaPlayer(Media(classLoader.getResource("sounds/fastinvader1.wav")?.toString())).play()
                    }
                    else if (abs(ENEMY_SPEED) <= 4.0) {
                        MediaPlayer(Media(classLoader.getResource("sounds/fastinvader2.wav")?.toString())).play()
                    }
                    else {
                        MediaPlayer(Media(classLoader.getResource("sounds/fastinvader3.wav")?.toString())).play()
                    }
                    for (alien in alienGroup.children) {
                        (alien as ImageView).y += enemyHeight
                        // if we (the aliens) reach the bottom of the screen or if we touch the player, we lose a life
                        if ((((alien as ImageView).y > scene.height - enemyHeight - 10.0) or (alien.boundsInParent.intersects(player.boundsInParent)))) {
                            model.loseLife()
                            MediaPlayer(Media(classLoader.getResource("sounds/explosion.wav")?.toString())).play()

                            // did we die
                            if (lives == 0) {
                                println("aliens have descended onto earth :(")
                                model.setScene(SCENES.GAMEOVERSCENE)
                                playerTimer.stop()
                                playerBulletSpamTimer.stop()
                                this.stop()
                                break
                            }
                            // we did not
                            else {
                                // find a safe place to respawn
                                var respawn = false
                                while (!respawn) {
                                    player.x = Random.nextDouble(10.0, scene.width - playerWidth - 10.0 )
                                    respawn = true
                                    for (alien in alienGroup.children) {
                                        if (alien.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                    for (bullet in alienBullets) {
                                        if (bullet.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // shoot a bullet
                    if (alienGroup.children.size > 0) {
                        println(alienBulletCount)
                        // get a random alien and make it shoot a bullet
                        val nextAlien = alienGroup.children.elementAt(Random.nextInt(0,alienGroup.children.size))
                        var enemyBullet = ImageView("images/player_bullet.png")
                        // see what type of alien it is and make it fire the bullet
                        if (greenAliens.contains(nextAlien.id)) {
                            enemyBullet = ImageView("images/bullet3.png")
                            enemyBullet.x = (nextAlien as ImageView).x + 21.0
                            enemyBullet.y = nextAlien.y + enemyHeight
                        }
                        else if (blueAliens.contains(nextAlien.id)) {
                            enemyBullet = ImageView("images/bullet2.png")
                            enemyBullet.x = (nextAlien as ImageView).x + 21.0
                            enemyBullet.y = nextAlien.y + enemyHeight
                        }
                        else if (purpleAliens.contains(nextAlien.id)) {
                            enemyBullet = ImageView("images/bullet1.png")
                            enemyBullet.x = (nextAlien as ImageView).x + 21.5
                            enemyBullet.y = nextAlien.y + enemyHeight
                        }
                        MediaPlayer(Media(classLoader.getResource("sounds/shoot.wav")?.toString())).play()
                        // animate each bullet using an animation timer
                        val enemyBulletTimer: AnimationTimer = object : AnimationTimer() {
                            override fun handle(now: Long) {
                                enemyBullet.y += ENEMY_BULLET_SPEED // animate parameter
                                // check if the bullet hit any enemies
                                if (enemyBullet.boundsInParent.intersects(player.boundsInParent)) {
                                    model.loseLife()
                                    MediaPlayer(Media(classLoader.getResource("sounds/explosion.wav")?.toString())).play()

                                    if (lives == 0) {
                                        println("you seem to have been shot to death :(")
                                        this.stop()
                                        playerTimer.stop()
                                        playerBulletSpamTimer.stop()
                                        model.setScene(SCENES.GAMEOVERSCENE)
                                    }
                                    // find a safe place to respawn
                                    var respawn = false
                                    while (!respawn) {
                                        player.x = Random.nextDouble(10.0, scene.width - playerWidth - 10.0 )
                                        respawn = true
                                        for (alien in alienGroup.children) {
                                            if (alien.boundsInParent.intersects(player.boundsInParent)) {
                                                respawn = false
                                            }
                                        }
                                        for (bullet in alienBullets) {
                                            if (bullet.boundsInParent.intersects(player.boundsInParent)) {
                                                respawn = false
                                            }
                                        }
                                    }
                                    alienBullets.remove(enemyBullet)
                                    children.remove(enemyBullet)
                                    this.stop()
                                }
                                if (enemyBullet.y > scene.height) {
                                    alienBulletCount -= 1
                                    alienBullets.remove(enemyBullet)
                                    children.remove(enemyBullet)
                                    this.stop()
                                }
                            }
                        }
                        enemyBulletTimer.start()
                        alienBulletCount += 1
                        alienBullets.add(enemyBullet)
                        enemyBullet.toBack()
                        children.add(enemyBullet)
                    }
                }
            }
        }
        alienTimer.start()

            // create a bullet that fires from a random enemy after an arbitrary amount of time (within the next 5 seconds)
            val enemyBulletPauseTimer = PauseTransition(Duration(Random.nextDouble(1000.0, 5000.0)))
            // set timer finished event
            enemyBulletPauseTimer.onFinished = EventHandler {
                // don't fire a new bullet if there are already 3 (too many to dodge!!)
                if (alienBulletCount < 3 && alienGroup.children.size > 0) {
                    println(alienBulletCount)
                    // get a random alien and make it shoot a bullet
                    val nextAlien = alienGroup.children.elementAt(Random.nextInt(0,alienGroup.children.size))
                    var enemyBullet = ImageView("images/player_bullet.png")
                    // see what type of alien it is and make it fire the bullet
                    if (greenAliens.contains(nextAlien.id)) {
                        enemyBullet = ImageView("images/bullet3.png")
                        enemyBullet.x = (nextAlien as ImageView).x + 21.0
                        enemyBullet.y = nextAlien.y + enemyHeight
                    }
                    else if (blueAliens.contains(nextAlien.id)) {
                        enemyBullet = ImageView("images/bullet2.png")
                        enemyBullet.x = (nextAlien as ImageView).x + 21.0
                        enemyBullet.y = nextAlien.y + enemyHeight
                    }
                    else if (purpleAliens.contains(nextAlien.id)) {
                        enemyBullet = ImageView("images/bullet1.png")
                        enemyBullet.x = (nextAlien as ImageView).x + 21.5
                        enemyBullet.y = nextAlien.y + enemyHeight
                    }
                    MediaPlayer(Media(classLoader.getResource("sounds/shoot.wav")?.toString())).play()
                    // animate each bullet using an animation timer
                    val enemyBulletTimer: AnimationTimer = object : AnimationTimer() {
                        override fun handle(now: Long) {
                            enemyBullet.y += ENEMY_BULLET_SPEED // animate parameter
                            // check if the bullet hit any enemies
                            if (enemyBullet.boundsInParent.intersects(player.boundsInParent)) {
                                model.loseLife()
                                MediaPlayer(Media(classLoader.getResource("sounds/explosion.wav")?.toString())).play()

                                if (lives == 0) {
                                    println("you seem to have been shot to death :(")
                                    this.stop()
                                    playerTimer.stop()
                                    playerBulletSpamTimer.stop()
                                    alienTimer.stop()
                                    model.setScene(SCENES.GAMEOVERSCENE)
                                }
                                // find a safe place to respawn
                                var respawn = false
                                while (!respawn) {
                                    player.x = Random.nextDouble(10.0, scene.width - playerWidth - 10.0 )
                                    respawn = true
                                    for (alien in alienGroup.children) {
                                        if (alien.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                    for (bullet in alienBullets) {
                                        if (bullet.boundsInParent.intersects(player.boundsInParent)) {
                                            respawn = false
                                        }
                                    }
                                }
                                alienBullets.remove(enemyBullet)
                                children.remove(enemyBullet)
                                this.stop()
                            }
                            if (enemyBullet.y > scene.height) {
                                alienBulletCount -= 1
                                this.stop()
                            }
                        }
                    }
                    enemyBulletTimer.start()
                    alienBulletCount += 1
                    alienBullets.add(enemyBullet)
                    children.add(enemyBullet)
                    // if we still have lives, restart the timer and load more bullets >:3
                    if (lives > 0) {
                        enemyBulletPauseTimer.duration = Duration(Random.nextDouble(1000.0, 5000.0))
                        enemyBulletPauseTimer.playFromStart()
                    }
                }
            }
            enemyBulletPauseTimer.play()

        // add some event handlers for user input
        scene.addEventFilter(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
            when (event.code) {
                KeyCode.A -> {
                    playerTimerSpeed = -PLAYER_SPEED
                    playerLeft = true
                    println("I like to move it move it")
                }
                KeyCode.D -> {
                    playerTimerSpeed = PLAYER_SPEED
                    playerRight = true
                    println("I like to move it move it")
                }
                KeyCode.SPACE -> {
                    // check if we passed the spam timer interval
                    if (!PKFire) {
                        val playerBullet = ImageView("images/player_bullet.png")
                        // animate each bullet using an animation timer
                        val playerBulletTimer: AnimationTimer = object : AnimationTimer() {
                            override fun handle(now: Long) {
                                playerBullet.y -= PLAYER_BULLET_SPEED // animate parameter
                                if (playerBullet.y < 0.0) {
                                    children.remove(playerBullet)
                                    this.stop()
                                }
                                // check if the bullet hit any enemies
                                for (alien in alienGroup.children) {
                                    if (playerBullet.boundsInParent.intersects(alien.boundsInParent)) {
                                        MediaPlayer(Media(classLoader.getResource("sounds/invaderkilled.wav")?.toString())).play()
                                        if(greenAliens.contains(alien.id)) {
                                            model.increaseScore(30)
                                        }
                                        else if(blueAliens.contains(alien.id)) {
                                            model.increaseScore(20)
                                        }
                                        else if(purpleAliens.contains(alien.id)) {
                                            model.increaseScore(10)
                                        }
                                        children.remove(playerBullet)
                                        alienGroup.children.remove(alien)
                                        // if there are no more aliens left, we won this level !! proceed to the next one
                                        if (alienGroup.children.isEmpty()) {
                                            val level2 = Lvl2(model)
                                            model.setScene(SCENES.GAMESCENE2)
                                            this.stop()
                                            playerTimer.stop()
                                            playerBulletSpamTimer.stop()
                                            alienTimer.stop()
                                            break
                                        }
                                        // if not, just remove that enemy from our view
                                        else {
                                            // reset our references
                                            alienMinRef = Point2D(
                                                (alienGroup.children.first() as ImageView).x,
                                                (alienGroup.children.first() as ImageView).y
                                            )
                                            alienMaxRef = Point2D(
                                                (alienGroup.children.last() as ImageView).x + enemyWidth,
                                                (alienGroup.children.last() as ImageView).y + enemyHeight
                                            )
                                            ENEMY_SPEED *= 1.025
                                            this.stop()
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        playerBulletTimer.start()
                        children.add(playerBullet)
                        playerBullet.x = player.x + 45.0
                        playerBullet.y = player.y
                        println("PK FIRE !!!")
                        MediaPlayer(Media(classLoader.getResource("sounds/shoot.wav")?.toString())).play()
                        PKFire = true
                    }
                }
                else -> {}
            }
        }
        scene.addEventFilter(KeyEvent.KEY_RELEASED) { event: KeyEvent ->
            when (event.code) {
                KeyCode.A -> {
                    playerLeft = false
                    playerTimerSpeed = if (!playerRight) 0.0
                    else PLAYER_SPEED
                }
                KeyCode.D -> {
                    playerRight = false
                    playerTimerSpeed = if (!playerLeft) 0.0
                    else -PLAYER_SPEED
                }
                else -> {}
            }
        }

        children.addAll(scoreBoard, player, alienGroup)
        model.addView(this)
    }
}