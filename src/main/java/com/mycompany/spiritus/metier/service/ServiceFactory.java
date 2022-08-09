/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.spiritus.metier.service;


/**
 *
 * @author nikitaterekhov
 */
public class ServiceFactory {
    
    public static Service buildService(String serviceType) {
        if (serviceType.equals("HR")) {
            return new AccountService();
        }
        if (serviceType.equals("Planinng")) {
            return new PlanningService();
        }
        return null;
    }
}
