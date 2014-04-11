/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.DefaultFormatter;

/**
 *
 * @author esteban
 */
public class PatternFormatter extends DefaultFormatter {

    private Pattern pattern;

    public PatternFormatter(String pattern) throws PatternSyntaxException {
        this.pattern = Pattern.compile(pattern);
        this.setCommitsOnValidEdit(true);
    }

    
    
    @Override
    public Object stringToValue(String text) throws ParseException {

        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);

            if (matcher.matches()) {
                return super.stringToValue(text);
            }
            throw new ParseException("Pattern did not match", 0);
        }
        return text;
    }
}
