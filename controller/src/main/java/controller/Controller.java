package controller;

import controller.exception.ControllerException;
import query.Query;
import response.Response;

public interface Controller {
    Response handle(Query query) throws ControllerException;
}