package com.cognitive.simulator;


import java.util.Deque;
import java.util.ArrayDeque;

import org.drools.time.SessionPseudoClock;

import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.*;
import com.cognitive.data.*;
$imports:{importLine|$importLine$$\n$}$

global JCpSimDataManager jCPSimClient;
global SessionPseudoClock clock;
global Long sampleRate;

$globals:{global|$global$$\n$}$

rule "init Scenario"
salience 100
when
then
    $globalInitCodes:{globalInitCode|$globalInitCode$$\n$}$
    $initialInserts:{initialInsert|$initialInsert$$\n$}$
end

rule "[Sim] Modify Value"
salience -9
when
    \$token: ChangeValueToken(\$target: target, \$notifyChange: notifyChange, \$salience: salience)
    not ChangeValueToken(salience > \$salience)
then
    
    JCpSimData \$data = jCPSimClient.getData();
    Double \$oldValue = \$data.get(\$target);
    
    \$token.applyChange(\$data);
    
    Double \$newValue = \$data.get(\$target);
    
    String \$source = \$token.getSource();
    
    //TODO these checks should be rules
    if (\$target == JCpSimParameter.P_SHUNT && \$newValue < 3){
        \$newValue = 3.0;
        \$source = \$source + " OVERWRITTEN"; 
    } else if (\$target == JCpSimParameter.P_OPENING_PRESSURE && \$newValue < 3){
        \$newValue = 3.0;
        \$source = \$source + " OVERWRITTEN"; 
    } else if (\$target == JCpSimParameter.P_COMPLIANCE && \$newValue < 0.01){
        \$newValue = 0.01;
        \$source = \$source + " OVERWRITTEN"; 
    }
    
    
    jCPSimClient.set(\$target, \$newValue);
    
    retract(\$token);
    if (\$notifyChange){
        insert (new ValueModifiedToken(\$target, \$oldValue, jCPSimClient.getData().get(\$target), \$source));
    }
end

$rules:{rule|$rule$$\n$}$

$if(simulationTime)$
rule "Terminate scenario"
timer (int: $simulationTime$ 0s)
when
then
    System.out.println("Exiting scenario at "+kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime());
    kcontext.getKnowledgeRuntime().halt();
end
$endif$
