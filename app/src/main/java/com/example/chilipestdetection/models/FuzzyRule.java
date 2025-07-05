package com.example.chilipestdetection.models;

import java.util.List;

public class FuzzyRule {
    public List<String> symptoms; // List of required symptoms
    public double membershipValue; // Membership value for this rule

    public FuzzyRule(List<String> symptoms, double membershipValue) {
        this.symptoms = symptoms;
        this.membershipValue = membershipValue;
    }
}
