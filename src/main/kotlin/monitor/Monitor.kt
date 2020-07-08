package monitor

import kotlinx.coroutines.Job

interface Monitor {
  fun monitor(): Job
}