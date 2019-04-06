package com.maneletorres.safebites.utils;

import com.maneletorres.safebites.entities.Product;

public interface AsyncResponse {
    void processFinish(Product output);
}
