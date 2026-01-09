package de.ljz.questify.core.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeSeconds(): Long = NSDate().timeIntervalSince1970.toLong()
