import com.google.inject.{Guice, AbstractModule}
import play.api.GlobalSettings
import services._

/**
 * Created by Administrator on 2015/5/19.
 */
object Global extends GlobalSettings{

  /**
   * Bind types such that whenever UUIDGenerator is required, an instance of SimpleUUIDGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[UUIDGenerator]).to(classOf[SimpleUUIDGenerator])
      bind(classOf[PasswordEncoder]).to(classOf[Md5PasswordEncoder])
      bind(classOf[TaskService]).to(classOf[TaskServiceImpl])
      bind(classOf[EventService]).to(classOf[EventServiceImpl])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

}
