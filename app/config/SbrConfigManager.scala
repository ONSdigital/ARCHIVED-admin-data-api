package config

import com.typesafe.config.Config
import play.api.Logger

/**
 * Created by coolit on 19/07/2017.
 */
object SbrConfigManager {
  def envConf(conf: Config): Config = {
    // Get the environment variable 'environment' that we pass in
    val env = sys.props.get("environment").getOrElse("default")
    Logger.info(s"Load config for [$env] env")
    val envConf = conf.getConfig(s"env.$env")
    Logger.debug(envConf.toString)
    envConf
  }
}
