//package ru.shipcollision.api;
//
//import com.github.javafaker.Faker;
//import org.mockito.AdditionalMatchers;
//import org.mockito.Mockito;
//import ru.shipcollision.api.exceptions.InvalidCredentialsException;
//import ru.shipcollision.api.exceptions.NotFoundException;
//import ru.shipcollision.api.models.User;
//import ru.shipcollision.api.services.UserServiceImpl;
//
//import java.util.Random;
//
//@SuppressWarnings("PublicField")
//public class CorrectUserHelper {
//
//    public static Long id;
//
//    public static String username;
//
//    public static String email;
//
//    public static int rank;
//
//    public static String password;
//
//    static {
//        final Faker faker = new Faker();
//
//        id = (long) 1;
//        username = faker.name().username();
//        email = faker.internet().emailAddress();
//        rank = 1;
//        password = faker.internet().password();
//    }
//
//    public static User getCorrectUser() {
//        final User correctUser = new User();
//        correctUser.setId(id);
//        correctUser.setUsername(username);
//        correctUser.setEmail(email);
//        correctUser.setRank(rank);
//        correctUser.setPassword(password);
//        return correctUser;
//    }
//
//    public static User getRandomCorrectUser() {
//        final Faker faker = new Faker();
//        final Random random = new Random();
//
//        final User user = new User();
//        user.setUsername(faker.name().username());
//        user.setEmail(faker.internet().emailAddress());
//        final int bound = 100500;
//        user.setRank(random.nextInt(bound));
//        user.setPassword(faker.internet().password());
//        return user;
//    }
//
//    public static void mockUserService(UserServiceImpl userService) {
//        final User correctUser = CorrectUserHelper.getCorrectUser();
//
//        // UserService будет отвечать успешно, только если кидать ему на вход
//        // correctUser'a или его атрибуты. В остальных случаях будет исключение.
//        Mockito.when(userService.hasEmail(correctUser.getEmail()))
//                .thenReturn(true);
//        Mockito.when(userService.hasEmail(AdditionalMatchers.not(Mockito.eq(correctUser.getEmail()))))
//                .thenThrow(InvalidCredentialsException.class);
//
//        Mockito.when(userService.hasId(correctUser.getId()))
//                .thenReturn(true);
//        Mockito.when(userService.hasId(AdditionalMatchers.not(Mockito.eq(correctUser.getId()))))
//                .thenThrow(InvalidCredentialsException.class);
//
//        Mockito.when(userService.hasUser(correctUser))
//                .thenReturn(true);
//        Mockito.when(userService.hasUser(AdditionalMatchers.not(Mockito.eq(correctUser))))
//                .thenThrow(InvalidCredentialsException.class);
//
//        Mockito.when(userService.hasUsername(correctUser.getUsername()))
//                .thenReturn(true);
//        Mockito.when(userService.hasUsername(AdditionalMatchers.not(Mockito.eq(correctUser.getUsername()))))
//                .thenThrow(InvalidCredentialsException.class);
//
//        Mockito.when(userService.findById(correctUser.getId()))
//                .thenReturn(correctUser);
//        Mockito.when(userService.findById(AdditionalMatchers.not(Mockito.eq(correctUser.getId()))))
//                .thenThrow(NotFoundException.class);
//
//        Mockito.when(userService.findByEmail(correctUser.getEmail()))
//                .thenReturn(correctUser);
//        Mockito.when(userService.findByEmail(AdditionalMatchers.not(Mockito.eq(correctUser.getEmail()))))
//                .thenThrow(NotFoundException.class);
//    }
//
//    /**
//     * Класс для тестов, чтобы не ломать ограничения модели User.
//     */
//    @SuppressWarnings({"PublicField", "InnerClassFieldHidesOuterClassField"})
//    public static class ProxyUser {
//
//        public String username;
//
//        public String email;
//
//        public String password;
//
//        public ProxyUser(String username, String email, String password) {
//            this.username = username;
//            this.email = email;
//            this.password = password;
//        }
//
//        public static ProxyUser fromUser(User user) {
//            return new ProxyUser(user.getUsername(), user.getEmail(), user.getPassword());
//        }
//    }
//}
