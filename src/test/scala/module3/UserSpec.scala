package module3

import module3.emailService.EmailService.EmailService
import module3.emailService.{Email, EmailAddress, EmailService, EmailServiceMock, Html}
import module3.userDAO.UserDAOMock
import module3.userService.{User, UserID, UserService}
import zio.console.Console
import zio.{Has, ZIO, ZLayer}
import zio.test.Assertion.{anything, equalTo, isUnit}
import zio.test.environment.TestConsole
import zio.test.mock.Expectation.{unit, value}
import zio.test.{DefaultRunnableSpec, ZSpec, ZTestEnv, assertM, suite, testM}
import zio.test._

object UserSpec extends DefaultRunnableSpec{
  override def spec = suite("User spec")(
    testM("notify user"){
      val daoMock = UserDAOMock.FindBy(equalTo(UserID(1)), value(Some(User(UserID(1), EmailAddress("test@test.com")))))
      val emailServiceMock = EmailServiceMock.SendEmail(equalTo(Email(EmailAddress("test@test.com"), Html("Hello here"))), unit)
      val email = Email(EmailAddress("test@test.com"), Html("Hello there"))

      val layer = daoMock >>> UserService.live ++ emailServiceMock

      (for{
        _ <- UserService.notifyUser(UserID(1))
        value <- TestConsole.output
      } yield assert(value)(anything))
        .provideSomeLayer[TestConsole with Console](layer)
    }
  )
}
