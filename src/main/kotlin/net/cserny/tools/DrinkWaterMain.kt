package net.cserny.tools

import java.awt.*
import java.awt.event.ActionListener
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

fun main() {
    val resource = Thread.currentThread().contextClassLoader.getResource("glass.png")
    val icon = Toolkit.getDefaultToolkit().getImage(resource)
    val trayIcon = TrayIcon(icon, "Drink More Water")
    val drinkWater = TrayDrinkWater(trayIcon)
    drinkWater.init()
    SystemTray.getSystemTray().add(trayIcon)
    drinkWater.start(10000, 3600000)
}

interface DrinkWater {
    fun init()
    fun start(iterateMs: Long, triggerMs: Long)
    fun exit()
    fun toggleMute()
    fun isNotMuted(): Boolean
    fun triggerNotification()
}

class TrayDrinkWater(private val trayIcon: TrayIcon) : DrinkWater {

    private val popupMenu = PopupMenu()
    private val exitMenuItem = MenuItem("Exit")
    private val pauseMenuItem = CheckboxMenuItem("Pause")

    private var muted = false
    private var lastTriggerDate = LocalDateTime.now()

    override fun init() {
        trayIcon.isImageAutoSize = true

        exitMenuItem.addActionListener { exit() }
        pauseMenuItem.addItemListener { toggleMute() }

        popupMenu.add(exitMenuItem)
        popupMenu.add(pauseMenuItem)

        trayIcon.popupMenu = popupMenu
    }

    override fun start(iterateMs: Long, triggerMs: Long) {
        Timer().schedule(0, iterateMs) {
            val currentDate = LocalDateTime.now()
            if (ChronoUnit.MILLIS.between(lastTriggerDate, currentDate) >= triggerMs && isNotMuted()) {
                lastTriggerDate = currentDate
                triggerNotification()
            }
        }
    }

    override fun exit() {
        exitProcess(0)
    }

    override fun toggleMute() {
        muted = !muted
        pauseMenuItem.state = muted
    }

    override fun isNotMuted(): Boolean = !muted

    override fun triggerNotification() {
        trayIcon.displayMessage("Drink Water Notification",
                "An hour has passed, you need to drink some water!", TrayIcon.MessageType.INFO)
    }
}
