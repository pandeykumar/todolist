package controllers

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.Task
import play.api.libs.concurrent.Execution.Implicits._
import java.util.concurrent.TimeUnit
import scala.concurrent.Future

object Application extends Controller {
  
  val taskForm = Form(
    "label" -> nonEmptyText
  )
  
  def index = Action {
    //Ok(views.html.index("Your new application is ready!."))
    Redirect(routes.Application.tasks)
  }
  
  def tasks = Action {
    //Ok(views.html.index(Task.all(), taskForm))
    Ok(views.html.index(Task.all(), taskForm))
  }
  
  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
    		errors => BadRequest(views.html.index(Task.all(), errors)),
    		label => {
    		  Task.create(label)
    		  Redirect(routes.Application.tasks)
    		}
    )
  }
  
  def deleteTask(id: Long) = Action {
    Task.delete(id)
    Redirect(routes.Application.tasks)
  }
  
  
  def asyncTask = Action {
    Logger.info("In async action asyncTask ");
    val futureInt = scala.concurrent.Future { intensiveComputation()}
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 9L, TimeUnit.SECONDS)
    Async {
      Future.firstCompletedOf(Seq(futureInt, timeoutFuture)).map { 
      	case i: Int => {
      		//Logger.info("Matched int %s", i)
      		Ok("Got result: " + i)
      	}
      	case t: String => {
      	  Logger.info("Matched t : {$t} ") 
      	  InternalServerError(t)
      	}
      }  
    }    
  }
  
  def intensiveComputation() = {
	  for( i <- 1 until 10){
	    Thread.sleep(1000);
	  }
	  10
  }
  
}