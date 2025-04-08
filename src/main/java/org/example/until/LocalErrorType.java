package org.example.until;

public enum LocalErrorType {
    AUTH_FAILED("1001", "Помилка авторизації"),
    INVALID_PUBLIC_KEY("1002", "Невірний публічний ключ"),
    USER_NOT_FOUND("1003", "Користувача не знайдено"),
    INVALID_PASSWORD("1004", "Невірний пароль"),
    KEY_COMPARISON_ERROR("1005", "Помилка під час порівняння ключа"),
    PUBLIC_KEY_MISSING("1006", "Відсутній публічний ключ"),
    KEY_SAVE_ERROR("1007", "Помилка збереження публічного ключа"),
    KEY_LOAD_ERROR("1008", "Помилка завантаження публічного ключа"),
    KEY_RETRIEVAL_ERROR("1009", "Помилка отримання ключа"),
    REGISTRATION_ERROR("1010", "Помилка реєстрації"),
    USER_NAME_ALREADY_EXISTS("1011", "Користувача за даним User імʼям вже заєрестрований"),
    USER_EMAIL_ALREADY_EXISTS("1012", "Користувача за данною електроною поштою вже заєрестрований"),
    ENCRYPT_FAILED("1013", "Помилка під час шифрування.");

    private final String code;
    private final String message;

    LocalErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
