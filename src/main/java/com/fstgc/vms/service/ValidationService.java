package com.fstgc.vms.service;

import java.util.regex.Pattern;

public class ValidationService {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    public boolean isValidEmail(String email) { return email != null && EMAIL.matcher(email).matches(); }
    public boolean isValidPhone(String phone) { return phone != null && PHONE.matcher(phone).matches(); }
}
