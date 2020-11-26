package net.cserny.tools

import java.awt.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

fun main() {
    val resource = Thread.currentThread().contextClassLoader.getResource("glass.png")
    val icon = Toolkit.getDefaultToolkit().getImage(resource)
    val trayIcon = TrayIcon(icon, "Drink More Water")
    val drinkWater = TrayDrinkWater(trayIcon)
    drinkWater.start(10000, 3600000)
    SystemTray.getSystemTray().add(trayIcon)
}

class TrayDrinkWater(
    private val trayIcon: TrayIcon,
    private val popupMenu: PopupMenu = PopupMenu(),
    private val exitMenuItem: MenuItem = MenuItem("Exit"),
    private val pauseMenuItem: CheckboxMenuItem = CheckboxMenuItem("Pause"),
    private var muted: Boolean = false,
    private var lastTriggerDate: LocalDateTime = LocalDateTime.now()
) {

    fun start(iterateMs: Long, triggerMs: Long) {
        init()
        Timer().schedule(0, iterateMs) {
            val currentDate = LocalDateTime.now()
            if (ChronoUnit.MILLIS.between(lastTriggerDate, currentDate) >= triggerMs && !muted) {
                lastTriggerDate = currentDate
                triggerNotification()
            }
        }
    }

    private fun init() {
        trayIcon.isImageAutoSize = true

        exitMenuItem.addActionListener { exitProcess(0) }
        pauseMenuItem.addItemListener { toggleMute() }

        popupMenu.add(exitMenuItem)
        popupMenu.add(pauseMenuItem)

        trayIcon.popupMenu = popupMenu
    }

    private fun toggleMute() {
        muted = !muted
        pauseMenuItem.state = muted
    }

    private fun triggerNotification() {
        trayIcon.displayMessage(
            "Drink Water Notification",
            "An hour has passed, you need to drink some water!", TrayIcon.MessageType.INFO
        )
    }
}
