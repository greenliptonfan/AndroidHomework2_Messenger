package vonavi.messengerhw2.interfaces;

/**
 * Created by Валентин on 29.11.2015.
 */
public interface ErrorsFromServer {
    int ALREADY_EXIST = 1;
    int INVALID_PASS = 2;
    int INVALID_DATA = 3;
    int EMPTY_FIELD = 4;
    int ALREADY_REGISTER = 5;
    int NEED_AUTH = 6;
    int NEED_REGISTER = 7;
    int USER_NOT_FOUND = 8;
    int CHANNEL_NOT_FOUND = 9;
    int INVALID_CHANNEL = 10;

}
