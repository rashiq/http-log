package model

import java.io.File

data class Configuration(val logFile: File, val alertingThreshold: Int, val alertingWindow: Int)
