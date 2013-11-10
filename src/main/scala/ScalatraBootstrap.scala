import com.mongodb.casbah.Imports._
import net.groovygrevling.PubchessController
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    val mongoClient = MongoClient()
    val db = mongoClient("chess")
    val players = db("players")

    context.mount(new PubchessController(players), "/players/*")
  }
}
