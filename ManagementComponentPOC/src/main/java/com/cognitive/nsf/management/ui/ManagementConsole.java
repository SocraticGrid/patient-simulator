/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.ui;

import com.cognitive.SimulationExecutor;
import com.cognitive.nsf.management.Manager;
import com.cognitive.nsf.management.ManagerBuilder;
import com.cognitive.nsf.management.ManagerEventListener;
import com.cognitive.nsf.management.disease.ARDSDiseaseSimulationRules;
import com.cognitive.nsf.management.disease.AsthmaDiseaseSimulationRules;
import com.cognitive.nsf.management.disease.DiseaseSimulationRules;
import com.cognitive.nsf.management.disease.PneumoniaDiseaseSimulationRules;
import com.cognitive.nsf.management.fact.ConstraintViolation;
import com.cognitive.nsf.management.fact.control.DiseaseActionRequested;
import com.cognitive.nsf.management.fact.control.NoAlternativeModelFound;
import com.cognitive.nsf.management.fact.control.Phase;
import com.cognitive.nsf.management.log.FileLogger;
import com.cognitive.nsf.management.model.ARDSModel;
import com.cognitive.nsf.management.model.AsthmaModel;
import com.cognitive.nsf.management.model.DiseaseModel;
import com.cognitive.nsf.management.model.PneumoniaModel;
import com.cognitive.template.FreeFormRuleTemplate;
import com.cognitive.template.SimulationRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.jcpsim.gui.TopMenu;
import org.jcpsim.jmx.JCpSimTopMenuMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmtMBean;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;

/**
 *
 * @author esteban
 */
public class ManagementConsole extends javax.swing.JFrame {

    private String periodicalRulesRate = "60s";
    private long thresholdRulesThreshold = 60000;
    private static final Logger LOG = Logger.getLogger(ManagementConsole.class.getName());
    private ButtonGroup modelButtonGroup = new ButtonGroup();
    private ButtonGroup diseaseButtonGroup = new ButtonGroup();
    private ButtonGroup expectationButtonGroup = new ButtonGroup();
    private List<DiseaseModel> models;
    private DiseaseModel ardsModel;
    private DiseaseModel pneumoniaModel;
    private DiseaseModel asthmaModel;
    private JCpSimPollingClient jcpSimClient;
    private Manager manager;
    private SimulationExecutor activeDiseaseExecutor;
    private SimulationExecutor ardsDiseaseExecutor;
    private SimulationExecutor pneumoniaDiseaseExecutor;
    private SimulationExecutor asthmaDiseaseExecutor;
    private ManagementConsoleEventListener managementConsoleEventListener;
    private RuleListener ruleListener;
    private ScheduledExecutorService diseaseThreadExecutor = Executors.newScheduledThreadPool(1);
    private boolean running;
    private JCpSimTopMenuMgmtMBean topMenuMbean;
    private final transient StringTemplate executorRulesTemplate;
    private File mixedLogFile;
    private File diseaseLogFile;
    private File modelLogFile;

    /**
     * Creates new form ManagementConsole
     */
    public ManagementConsole() throws IOException, MalformedObjectNameException, InstanceNotFoundException, Exception {
        initComponents();

        managementConsoleEventListener = new ManagementConsoleEventListener(this);
        ruleListener = new RuleListener();


        ardsModel = new ARDSModel();
        pneumoniaModel = new PneumoniaModel();
        asthmaModel = new AsthmaModel();

        models = new ArrayList<DiseaseModel>();
        models.add(ardsModel);
        models.add(pneumoniaModel);
        models.add(asthmaModel);


        this.configureMBeans();

        SimulationExecutor.SimulationListener simulationListener = new SimulationExecutor.SimulationListener() {
            public void onStep(int time) {
            }

            public void onException(Exception e) {
                Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, e);
            }

            public void onPause() {
            }

            public void onTermination() {
            }
        };

        executorRulesTemplate = new StringTemplate(IOUtils.toString(SimulationTemplateEngine.class.getResourceAsStream("/manager/console/templates/executorRulesTemplate.tpl")));


        //ARDS Disease Simulation
        ardsDiseaseExecutor = this.createSimulationExecutor(
                "ARDS",
                new ARDSDiseaseSimulationRules(periodicalRulesRate, thresholdRulesThreshold),
                simulationListener);

        //Pneumonia Disease Simulation
        pneumoniaDiseaseExecutor = this.createSimulationExecutor(
                "Pneumonia",
                new PneumoniaDiseaseSimulationRules(periodicalRulesRate, thresholdRulesThreshold),
                simulationListener);


        //Asthma Disease Simulation
        asthmaDiseaseExecutor = this.createSimulationExecutor(
                "Asthma",
                new AsthmaDiseaseSimulationRules(periodicalRulesRate, thresholdRulesThreshold),
                simulationListener);

    }

    private SimulationExecutor createSimulationExecutor(String diseaseName, DiseaseSimulationRules diseaseSimulationRules, SimulationExecutor.SimulationListener simulationListener) {
        SimulationTemplateEngine engine = new SimulationTemplateEngine();
        for (SimulationRuleTemplate simulationRuleTemplate : diseaseSimulationRules.getRules()) {
            engine.addRuleTemplate(simulationRuleTemplate);
        }
        executorRulesTemplate.reset();
        executorRulesTemplate.setAttribute("disease", diseaseName);

        FreeFormRuleTemplate logFormRuleTemplate = new FreeFormRuleTemplate(executorRulesTemplate.toString());
        logFormRuleTemplate.addImport("com.cognitive.data.ValueModifiedToken");
        logFormRuleTemplate.addImport("com.cognitive.nsf.management.log.FileLogger");

        engine.addRuleTemplate(logFormRuleTemplate);
        return new SimulationExecutor(engine, simulationListener);
    }

    private void startSimulation() throws IOException {
        this.running = true;
        try {

            double threshold = Double.parseDouble(this.txtThreshold.getText());

            this.btnRunSimulation.setText("Simulation is Running...");

            this.ruleListener.reset();

            //start FileLogger
            modelLogFile = File.createTempFile("JCpSim-Model-", ".csv");
            diseaseLogFile = File.createTempFile("JCpSim-Disease-", ".csv");
            mixedLogFile = File.createTempFile("JCpSim-Mixed-", ".csv");


            FileLogger.init(modelLogFile.getAbsolutePath(), diseaseLogFile.getAbsolutePath(), mixedLogFile.getAbsolutePath());

            //Re-create jcpSimClient and manager
            jcpSimClient = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());

            //create ManagerEventListener
            ManagerEventListener managerEventListener = new ManagerEventListener() {
                public void onModelChanged(DiseaseModel newModel) {

                    btnModelARDS.setSelected(newModel.getName().equals("ARDS"));
                    btnModelPneumonia.setSelected(newModel.getName().equals("Pneumonia"));
                    btnModelAsthma.setSelected(newModel.getName().equals("Asthma"));

                    ruleListener.resetRow(newModel.getName());

                }

                public void onDiseaseActionRequested(DiseaseActionRequested actionRequest) {
                    switch (actionRequest.getAction()) {
                        case PAUSE_DISEASE:
                            pauseDisease();
                            break;
                        case RESUME_DISEASE:
                            resumeDisease();
                            break;
                    }
                }
            };

            //create manager
            manager = new ManagerBuilder()
                    .setModels(models)
                    .setDataManager(jcpSimClient)
                    .setSampleRate(100L)
                    .addExtraResource(ResourceFactory.newClassPathResource("manager/console/rules/logging.drl"), ResourceType.DRL)
                    .addExtraResource(ResourceFactory.newClassPathResource("manager/console/rules/ui.drl"), ResourceType.DRL)
                    .registerGlobal("ruleListener", ruleListener)
                    .addAgendaEventListener(managementConsoleEventListener)
                    .addWorkingMemoryEventListener(managementConsoleEventListener)
                    .setEventListener(managerEventListener)
                    .setThreshold(threshold)
                    .createManager();

            //Set the active model
            if (btnModelARDS.isSelected()) {
                manager.setActiveModel(ardsModel);
            } else if (btnModelPneumonia.isSelected()) {
                manager.setActiveModel(pneumoniaModel);
            } else if (btnModelAsthma.isSelected()) {
                manager.setActiveModel(asthmaModel);
            }

            //Set Active Diagnosis
            if (btnExpectationARDS.isSelected()) {
                manager.setActiveExpectations("ARDS Expectation");
            } else if (btnExpectationPneumonia.isSelected()) {
                manager.setActiveExpectations("Pneumonia Expectation");
            } else if (btnExpectationAsthma.isSelected()) {
                manager.setActiveExpectations("Asthma Expectation");
            }

            //enable/disable patient safety rules
            if (chkPatientSafety.isSelected()) {
                manager.enablePatientSafetyRules();
            } else {
                manager.disablePatientSafetyRules();
            }


            //enable/disable recommendations safety rules
            if (chkRecommendationSafety.isSelected()) {
                manager.enableRecommendationsSafetyRules();
            } else {
                manager.disableRecommendationsSafetyRules();
            }

            //select disease
            if (btnDiseaseARDS.isSelected()) {
                activeDiseaseExecutor = ardsDiseaseExecutor;
            } else if (btnDiseasePneumonia.isSelected()) {
                activeDiseaseExecutor = pneumoniaDiseaseExecutor;
            } else if (btnDiseaseAsthma.isSelected()) {
                activeDiseaseExecutor = asthmaDiseaseExecutor;
            }

            //start disease
            if (activeDiseaseExecutor != null) {
                scheduleDiseaseExecutor(activeDiseaseExecutor);
            }

            topMenuMbean.simulationStarted("abc");

            //start model
            manager.startGatheringData();

            //enable log buttons
            this.btnLogModel.setEnabled(true);
            this.btnLogDisease.setEnabled(true);
            this.btnLogMixed.setEnabled(true);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error starting simulation", e);
            stopSimulation();
        }

    }

    private void stopSimulation() throws IOException {
        this.running = false;
        topMenuMbean.simulationStopped("abc");
        FileLogger.getInstance().dispose();
        this.btnRunSimulation.setText("Run Simulation");

        if (jcpSimClient != null) {
            jcpSimClient.dispose();
        }

        if (manager != null) {
            manager.stopGatheringData();
        }

        if (activeDiseaseExecutor != null) {
            activeDiseaseExecutor.stop();
            activeDiseaseExecutor = null;
        }
    }

    private void pauseDisease() {
        if (activeDiseaseExecutor != null) {
            activeDiseaseExecutor.suspend();
            btnDiseaseNone.setSelected(true);
        }

    }

    private void resumeDisease() {
        if (activeDiseaseExecutor != null) {
            btnDiseaseAsthma.setSelected(activeDiseaseExecutor == asthmaDiseaseExecutor);
            btnDiseasePneumonia.setSelected(activeDiseaseExecutor == pneumoniaDiseaseExecutor);
            btnDiseaseARDS.setSelected(activeDiseaseExecutor == ardsDiseaseExecutor);
            activeDiseaseExecutor.unsuspend();
        }
    }

    private void scheduleDiseaseExecutor(final SimulationExecutor simulationExecutor) {

        if (!this.running) {
            return;
        }

        diseaseThreadExecutor.schedule(new Runnable() {
            public void run() {
                simulationExecutor.execute(jcpSimClient);
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    public String getSelectedVentilatorModelName() {
        if (this.btnModelARDS.isSelected()) {
            return "Low Volume";
        }
        if (this.btnModelPneumonia.isSelected()) {
            return "Moderate Volume";
        }
        if (this.btnModelAsthma.isSelected()) {
            return "High Volume";
        }

        return "None";
    }

    public String getSelectedDiagnosisName() {
        if (this.btnExpectationARDS.isSelected()) {
            return "ARDS";
        }
        if (this.btnExpectationPneumonia.isSelected()) {
            return "Pneumonia";
        }
        if (this.btnExpectationAsthma.isSelected()) {
            return "Asthma";
        }

        return "None";
    }

    public String getThreshold() {
        return this.txtThreshold.getText();
    }

    private void configureMBeans() throws Exception {

        //TopMenu
        JMXServiceURL serviceUrl = new JMXServiceURL(TopMenu.JMX_URL);
        JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);

        MBeanServerConnection connection = jmxc.getMBeanServerConnection();

        topMenuMbean = JMX.newMBeanProxy(connection, new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME),
                JCpSimTopMenuMgmtMBean.class, true);

    }

    public ButtonGroup getModelButtonGroup() {
        return modelButtonGroup;
    }

    public ButtonGroup getDiseaseButtonGroup() {
        return diseaseButtonGroup;
    }

    public ButtonGroup getExpectationButtonGroup() {
        return expectationButtonGroup;
    }

    public void logFactCount(Class[] topNFacts, Map<Class, Long> factCounter) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < topNFacts.length; i++) {
            Class c = topNFacts[i];
            if (c != null) {
                b.append(c.getSimpleName()).append(": ").append(factCounter.get(c)).append("  ");
            }
        }
        this.lblFactCount.setText(b.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pnlModels = new javax.swing.JPanel();
        btnModelPneumonia = new javax.swing.JToggleButton();
        btnModelARDS = new javax.swing.JToggleButton();
        btnModelAsthma = new javax.swing.JToggleButton();
        btnModelNone = new javax.swing.JToggleButton();
        lblCurrentPhase = new javax.swing.JLabel();
        pnlDiseases = new javax.swing.JPanel();
        btnDiseaseARDS = new javax.swing.JToggleButton();
        btnDiseasePneumonia = new javax.swing.JToggleButton();
        btnDiseaseAsthma = new javax.swing.JToggleButton();
        btnDiseaseNone = new javax.swing.JToggleButton();
        pnlSafety = new javax.swing.JPanel();
        chkPatientSafety = new javax.swing.JCheckBox();
        chkRecommendationSafety = new javax.swing.JCheckBox();
        btnRunSimulation = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        btnLogModel = new javax.swing.JButton();
        btnLogDisease = new javax.swing.JButton();
        btnLogMixed = new javax.swing.JButton();
        lblFactCount = new javax.swing.JLabel();
        pnlDiseases1 = new javax.swing.JPanel();
        btnExpectationARDS = new javax.swing.JToggleButton();
        btnExpectationPneumonia = new javax.swing.JToggleButton();
        btnExpectationAsthma = new javax.swing.JToggleButton();
        btnExpectationNone = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblConstraintViolations = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblConstraintViolationsDetails = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtThreshold = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Management Console");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        pnlModels.setBorder(javax.swing.BorderFactory.createTitledBorder("Ventilator Models"));

        getModelButtonGroup().add(btnModelPneumonia);
        btnModelPneumonia.setText("Mod. Volume");
        btnModelPneumonia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModelPneumoniaActionPerformed(evt);
            }
        });

        getModelButtonGroup().add(btnModelARDS);
        btnModelARDS.setText("Low Volume");
        btnModelARDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModelARDSActionPerformed(evt);
            }
        });

        getModelButtonGroup().add(btnModelAsthma);
        btnModelAsthma.setText("High Volume");
        btnModelAsthma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModelAsthmaActionPerformed(evt);
            }
        });

        getModelButtonGroup().add(btnModelNone);
        btnModelNone.setSelected(true);
        btnModelNone.setText("None");
        btnModelNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModelNoneActionPerformed(evt);
            }
        });

        lblCurrentPhase.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lblCurrentPhase.setText("Current Phase:");

        javax.swing.GroupLayout pnlModelsLayout = new javax.swing.GroupLayout(pnlModels);
        pnlModels.setLayout(pnlModelsLayout);
        pnlModelsLayout.setHorizontalGroup(
            pnlModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModelsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlModelsLayout.createSequentialGroup()
                        .addComponent(btnModelARDS, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModelPneumonia, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModelAsthma, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModelNone, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCurrentPhase))
                .addGap(0, 59, Short.MAX_VALUE))
        );

        pnlModelsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnModelAsthma, btnModelNone, btnModelPneumonia});

        pnlModelsLayout.setVerticalGroup(
            pnlModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlModelsLayout.createSequentialGroup()
                .addComponent(lblCurrentPhase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModelAsthma)
                    .addComponent(btnModelNone)
                    .addComponent(btnModelPneumonia)
                    .addComponent(btnModelARDS))
                .addContainerGap())
        );

        pnlDiseases.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Disease"));

        getDiseaseButtonGroup().add(btnDiseaseARDS);
        btnDiseaseARDS.setText("ARDS");
        btnDiseaseARDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiseaseARDSActionPerformed(evt);
            }
        });

        getDiseaseButtonGroup().add(btnDiseasePneumonia);
        btnDiseasePneumonia.setText("Pneumonia");
        btnDiseasePneumonia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiseasePneumoniaActionPerformed(evt);
            }
        });

        getDiseaseButtonGroup().add(btnDiseaseAsthma);
        btnDiseaseAsthma.setText("Asthma");
        btnDiseaseAsthma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiseaseAsthmaActionPerformed(evt);
            }
        });

        getDiseaseButtonGroup().add(btnDiseaseNone);
        btnDiseaseNone.setSelected(true);
        btnDiseaseNone.setText("None");
        btnDiseaseNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiseaseNoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDiseasesLayout = new javax.swing.GroupLayout(pnlDiseases);
        pnlDiseases.setLayout(pnlDiseasesLayout);
        pnlDiseasesLayout.setHorizontalGroup(
            pnlDiseasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiseasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDiseaseARDS, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDiseasePneumonia, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDiseaseAsthma, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDiseaseNone, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        pnlDiseasesLayout.setVerticalGroup(
            pnlDiseasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiseasesLayout.createSequentialGroup()
                .addGroup(pnlDiseasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnDiseaseNone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDiseaseAsthma, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDiseasePneumonia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDiseaseARDS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pnlSafety.setBorder(javax.swing.BorderFactory.createTitledBorder("Safety Rules"));

        chkPatientSafety.setSelected(true);
        chkPatientSafety.setText("Patient Safety");
        chkPatientSafety.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPatientSafetyActionPerformed(evt);
            }
        });

        chkRecommendationSafety.setSelected(true);
        chkRecommendationSafety.setText("Recommendations Safety");

        javax.swing.GroupLayout pnlSafetyLayout = new javax.swing.GroupLayout(pnlSafety);
        pnlSafety.setLayout(pnlSafetyLayout);
        pnlSafetyLayout.setHorizontalGroup(
            pnlSafetyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSafetyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPatientSafety)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkRecommendationSafety)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSafetyLayout.setVerticalGroup(
            pnlSafetyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSafetyLayout.createSequentialGroup()
                .addGroup(pnlSafetyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPatientSafety)
                    .addComponent(chkRecommendationSafety))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnRunSimulation.setText("Run Simulation");
        btnRunSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunSimulationActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jLabel1.setText("Log Files:");

        btnLogModel.setText("Ventilator Log");
        btnLogModel.setEnabled(false);
        btnLogModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogModelActionPerformed(evt);
            }
        });

        btnLogDisease.setText("Disease Log");
        btnLogDisease.setEnabled(false);
        btnLogDisease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogDiseaseActionPerformed(evt);
            }
        });

        btnLogMixed.setText("Combined Log");
        btnLogMixed.setEnabled(false);
        btnLogMixed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogMixedActionPerformed(evt);
            }
        });

        lblFactCount.setFont(new java.awt.Font("Ubuntu", 1, 10)); // NOI18N

        pnlDiseases1.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Diagnosis"));

        getExpectationButtonGroup().add(btnExpectationARDS);
        btnExpectationARDS.setText("ARDS");
        btnExpectationARDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpectationARDSActionPerformed(evt);
            }
        });

        getExpectationButtonGroup().add(btnExpectationPneumonia);
        btnExpectationPneumonia.setText("Pneumonia");
        btnExpectationPneumonia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpectationPneumoniaActionPerformed(evt);
            }
        });

        getExpectationButtonGroup().add(btnExpectationAsthma);
        btnExpectationAsthma.setText("Asthma");
        btnExpectationAsthma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpectationAsthmaActionPerformed(evt);
            }
        });

        getExpectationButtonGroup().add(btnExpectationNone);
        btnExpectationNone.setSelected(true);
        btnExpectationNone.setText("None");
        btnExpectationNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpectationNoneActionPerformed(evt);
            }
        });

        tblConstraintViolations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Low", "0", "0", "0", "0", "0", "0"},
                {"Mod.", "0", "0", "0", "0", "0", "0"},
                {"High", "0", "0", "0", "0", "0", "0"}
            },
            new String [] {
                "Model", "# Soft", "S. Weight", "# Hard", "H. Weight", "# Total", "T. Weight"
            }
        ));
        jScrollPane1.setViewportView(tblConstraintViolations);

        tblConstraintViolationsDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Constraint", "#"
            }
        ));
        jScrollPane2.setViewportView(tblConstraintViolationsDetails);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Threshold:");

        txtThreshold.setText("100");
        txtThreshold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtThresholdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDiseases1Layout = new javax.swing.GroupLayout(pnlDiseases1);
        pnlDiseases1.setLayout(pnlDiseases1Layout);
        pnlDiseases1Layout.setHorizontalGroup(
            pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiseases1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlDiseases1Layout.createSequentialGroup()
                        .addGroup(pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlDiseases1Layout.createSequentialGroup()
                                .addComponent(btnExpectationARDS, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExpectationPneumonia, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlDiseases1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89)))
                        .addComponent(btnExpectationAsthma, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExpectationNone, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlDiseases1Layout.setVerticalGroup(
            pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiseases1Layout.createSequentialGroup()
                .addGroup(pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDiseases1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnExpectationPneumonia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExpectationAsthma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExpectationNone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnExpectationARDS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlModels, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogModel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogDisease)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogMixed)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnRunSimulation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDiseases1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDiseases, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSafety, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFactCount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnLogDisease, btnLogMixed, btnLogModel});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnLogModel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogDisease, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogMixed, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRunSimulation, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlModels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDiseases1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDiseases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSafety, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFactCount, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkPatientSafetyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPatientSafetyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPatientSafetyActionPerformed

    private void btnRunSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunSimulationActionPerformed
        try {
            if (btnRunSimulation.isSelected()) {
                startSimulation();
            } else {
                stopSimulation();
            }
        } catch (IOException ex) {
            Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnRunSimulationActionPerformed

    private void btnDiseaseARDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiseaseARDSActionPerformed
        if (activeDiseaseExecutor != null) {
            this.activeDiseaseExecutor.stop();
        }
        this.activeDiseaseExecutor = ardsDiseaseExecutor;
        scheduleDiseaseExecutor(activeDiseaseExecutor);
    }//GEN-LAST:event_btnDiseaseARDSActionPerformed

    private void btnDiseasePneumoniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiseasePneumoniaActionPerformed
        if (activeDiseaseExecutor != null) {
            this.activeDiseaseExecutor.stop();
        }
        this.activeDiseaseExecutor = pneumoniaDiseaseExecutor;
        scheduleDiseaseExecutor(activeDiseaseExecutor);
    }//GEN-LAST:event_btnDiseasePneumoniaActionPerformed

    private void btnDiseaseAsthmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiseaseAsthmaActionPerformed
        if (activeDiseaseExecutor != null) {
            this.activeDiseaseExecutor.stop();
        }
        this.activeDiseaseExecutor = asthmaDiseaseExecutor;
        scheduleDiseaseExecutor(activeDiseaseExecutor);
    }//GEN-LAST:event_btnDiseaseAsthmaActionPerformed

    private void btnDiseaseNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiseaseNoneActionPerformed
        if (activeDiseaseExecutor != null) {
            this.activeDiseaseExecutor.stop();
        }
        this.activeDiseaseExecutor = null;
    }//GEN-LAST:event_btnDiseaseNoneActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void btnModelNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModelNoneActionPerformed
        if (manager != null) {
            this.manager.setActiveModel(null);
        }
        this.ruleListener.reset();
    }//GEN-LAST:event_btnModelNoneActionPerformed

    private void btnModelAsthmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModelAsthmaActionPerformed
        if (manager != null) {
            this.manager.setActiveModel(asthmaModel);
        }
        this.ruleListener.reset();
    }//GEN-LAST:event_btnModelAsthmaActionPerformed

    private void btnModelARDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModelARDSActionPerformed
        if (manager != null) {
            this.manager.setActiveModel(ardsModel);
        }
        this.ruleListener.reset();
    }//GEN-LAST:event_btnModelARDSActionPerformed

    private void btnModelPneumoniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModelPneumoniaActionPerformed
        if (manager != null) {
            this.manager.setActiveModel(pneumoniaModel);
        }
        this.ruleListener.reset();
    }//GEN-LAST:event_btnModelPneumoniaActionPerformed

    private void btnLogModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogModelActionPerformed
        try {
            Desktop.getDesktop().open(modelLogFile);
        } catch (IOException ex) {
            Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLogModelActionPerformed

    private void btnLogDiseaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogDiseaseActionPerformed
        try {
            Desktop.getDesktop().open(diseaseLogFile);
        } catch (IOException ex) {
            Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLogDiseaseActionPerformed

    private void btnLogMixedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogMixedActionPerformed
        try {
            Desktop.getDesktop().open(mixedLogFile);
        } catch (IOException ex) {
            Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLogMixedActionPerformed

    private void btnExpectationNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpectationNoneActionPerformed
        if (manager != null) {
            this.manager.disableExpectations();
        }
    }//GEN-LAST:event_btnExpectationNoneActionPerformed

    private void btnExpectationAsthmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpectationAsthmaActionPerformed
        if (manager != null) {
            this.manager.setActiveExpectations("Asthma Expectation");
        }
    }//GEN-LAST:event_btnExpectationAsthmaActionPerformed

    private void btnExpectationPneumoniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpectationPneumoniaActionPerformed
        if (manager != null) {
            this.manager.setActiveExpectations("Pneumonia Expectation");
        }
    }//GEN-LAST:event_btnExpectationPneumoniaActionPerformed

    private void btnExpectationARDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpectationARDSActionPerformed
        if (manager != null) {
            this.manager.setActiveExpectations("ARDS Expectation");
        }
    }//GEN-LAST:event_btnExpectationARDSActionPerformed

    private void txtThresholdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtThresholdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtThresholdActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManagementConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManagementConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManagementConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManagementConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ManagementConsole().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedObjectNameException ex) {
                    Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstanceNotFoundException ex) {
                    Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ManagementConsole.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnDiseaseARDS;
    private javax.swing.JToggleButton btnDiseaseAsthma;
    private javax.swing.JToggleButton btnDiseaseNone;
    private javax.swing.JToggleButton btnDiseasePneumonia;
    private javax.swing.JToggleButton btnExpectationARDS;
    private javax.swing.JToggleButton btnExpectationAsthma;
    private javax.swing.JToggleButton btnExpectationNone;
    private javax.swing.JToggleButton btnExpectationPneumonia;
    private javax.swing.JButton btnLogDisease;
    private javax.swing.JButton btnLogMixed;
    private javax.swing.JButton btnLogModel;
    private javax.swing.JToggleButton btnModelARDS;
    private javax.swing.JToggleButton btnModelAsthma;
    private javax.swing.JToggleButton btnModelNone;
    private javax.swing.JToggleButton btnModelPneumonia;
    private javax.swing.JToggleButton btnRunSimulation;
    private javax.swing.JCheckBox chkPatientSafety;
    private javax.swing.JCheckBox chkRecommendationSafety;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCurrentPhase;
    private javax.swing.JLabel lblFactCount;
    private javax.swing.JPanel pnlDiseases;
    private javax.swing.JPanel pnlDiseases1;
    private javax.swing.JPanel pnlModels;
    private javax.swing.JPanel pnlSafety;
    private javax.swing.JTable tblConstraintViolations;
    private javax.swing.JTable tblConstraintViolationsDetails;
    private javax.swing.JFormattedTextField txtThreshold;
    // End of variables declaration//GEN-END:variables

    public class RuleListener {

        private static final int MODEL_COUNT = 3;
        private final Map<String, Integer> rowIndex = new HashMap<String, Integer>();
        private final long[] softConstraintsCount = new long[MODEL_COUNT];
        private final double[] softConstraintsTotalWeight = new double[MODEL_COUNT];
        private final long[] hardConstraintsCount = new long[MODEL_COUNT];
        private final double[] hardConstraintsTotalWeight = new double[MODEL_COUNT];

        private class ConstraintViolationDetail {

            String type;
            String constraint;
            long count;
        }
        private final Map<String, Map<String, ConstraintViolationDetail>> constraintViolationDetails = new HashMap<String, Map<String, ConstraintViolationDetail>>();

        public RuleListener() {
            rowIndex.put("ARDS", 0);
            rowIndex.put("Pneumonia", 1);
            rowIndex.put("Asthma", 2);

            tblConstraintViolations.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    int row = tblConstraintViolations.rowAtPoint(e.getPoint());
                    String model;
                    switch (row) {
                        case 0:
                            model = "ARDS";
                            break;
                        case 1:
                            model = "Pneumonia";
                            break;
                        case 2:
                            model = "Asthma";
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown row: " + row);
                    }

                    List<ConstraintViolationDetail> details = new ArrayList<ConstraintViolationDetail>(constraintViolationDetails.get(model).values());
                    Collections.sort(details, new Comparator<ConstraintViolationDetail>() {
                        public int compare(ConstraintViolationDetail o1, ConstraintViolationDetail o2) {
                            return o1.count >= o2.count ? -1 : 1;
                        }
                    });

                    for (int i = tblConstraintViolationsDetails.getModel().getRowCount() - 1; i >= 0; i--) {
                        ((DefaultTableModel) tblConstraintViolationsDetails.getModel()).removeRow(i);
                    }
                    for (ConstraintViolationDetail d : details) {
                        ((DefaultTableModel) tblConstraintViolationsDetails.getModel()).addRow(new Object[]{d.type, d.constraint, d.count});
                    }

                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });

            reset();
        }

        public void onConstraintViolation(ConstraintViolation cv) {
            //update the counters and values
            if (cv.getType() == ConstraintViolation.TYPE.SOFT) {
                softConstraintsCount[rowIndex.get(cv.getModel().getName())]++;
                softConstraintsTotalWeight[rowIndex.get(cv.getModel().getName())] += cv.getWeight();
            } else {
                hardConstraintsCount[rowIndex.get(cv.getModel().getName())]++;
                hardConstraintsTotalWeight[rowIndex.get(cv.getModel().getName())] += cv.getWeight();
            }

            //update details
            ConstraintViolationDetail constraintViolationDetail = constraintViolationDetails.get(cv.getModel().getName()).get(cv.getSource());
            if (constraintViolationDetail == null) {
                constraintViolationDetail = new ConstraintViolationDetail();
                constraintViolationDetail.constraint = cv.getSource();
                constraintViolationDetail.type = cv.getType().name();
                constraintViolationDetails.get(cv.getModel().getName()).put(cv.getSource(), constraintViolationDetail);
            }
            constraintViolationDetail.count++;


            //refresh table
            this.refreshTable();
        }

        public void onConstraintViolationCalculationResult(DiseaseModel model, Double totalWeight, Set<ConstraintViolation> constraintsViolations) {
            int index = this.rowIndex.get(model.getName());
            this.resetRow(model.getName());

            Map<String, ConstraintViolationDetail> cvs = this.constraintViolationDetails.get(model.getName());
            if (cvs != null) {
                cvs.clear();
            }

            for (ConstraintViolation cv : constraintsViolations) {
                this.onConstraintViolation(cv);
            }

        }

        public void onPhaseChange(Phase phase) {
            lblCurrentPhase.setText("Current Phase: " + phase.getName().getFriendlyName());
        }

        public void onNoAlternativeModelFound(NoAlternativeModelFound event) {

            NoPerformantModelFoundPanel panel = new NoPerformantModelFoundPanel(ManagementConsole.this, event);

            JOptionPane.showOptionDialog(ManagementConsole.this, panel, "...", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Go"}, "Go");

            if (panel.isContinueWithVentilatorModelSelected()) {
                String selectedModel = panel.getSelectedVentilatorModel();

                //Set the selected Ventilator model
                if ("High".equals(selectedModel)) {
                    btnModelAsthma.doClick();
                } else if ("Moderate".equals(selectedModel)) {
                    btnModelPneumonia.doClick();
                } else if ("Low".equals(selectedModel)) {
                    btnModelARDS.doClick();
                }

                //Change phase
                manager.goToPhase(Phase.PhaseName.EXTENDED_STABILIZATION);

            } else if (panel.isContinueWithNewDiagnosisSelected()) {

                //Set the selected Diagnosis
                String selectedDiagnosis = panel.getSelectedDiagnosis();
                if ("ARDS".equals(selectedDiagnosis)) {
                    btnExpectationARDS.doClick();
                } else if ("Pneumonia".equals(selectedDiagnosis)) {
                    btnExpectationPneumonia.doClick();
                } else if ("Asthma".equals(selectedDiagnosis)) {
                    btnExpectationAsthma.doClick();
                }

                //Reset to initial phase
                manager.goToInitialPhase();

                //clean constraint violations table
                this.reset();
                
                //Resume the disease simulator
                resumeDisease();
            }

        }

        public void reset() {
            for (String model : rowIndex.keySet()) {
                this.resetRow(model);
            }
        }

        private void resetRow(String model) {
            int index = this.rowIndex.get(model);
            softConstraintsCount[index] = 0;
            softConstraintsTotalWeight[index] = 0;
            hardConstraintsCount[index] = 0;
            hardConstraintsTotalWeight[index] = 0;
            constraintViolationDetails.put(model, new LinkedHashMap<String, ConstraintViolationDetail>());
            refreshTable();
        }

        private void refreshTable() {
            for (Map.Entry<String, Integer> entry : this.rowIndex.entrySet()) {
                tblConstraintViolations.getModel().setValueAt(softConstraintsCount[entry.getValue()], entry.getValue(), 1);
                tblConstraintViolations.getModel().setValueAt(softConstraintsTotalWeight[entry.getValue()], entry.getValue(), 2);
                tblConstraintViolations.getModel().setValueAt(hardConstraintsCount[entry.getValue()], entry.getValue(), 3);
                tblConstraintViolations.getModel().setValueAt(hardConstraintsTotalWeight[entry.getValue()], entry.getValue(), 4);
                tblConstraintViolations.getModel().setValueAt(softConstraintsCount[entry.getValue()] + hardConstraintsCount[entry.getValue()], entry.getValue(), 5);
                tblConstraintViolations.getModel().setValueAt(softConstraintsTotalWeight[entry.getValue()] + hardConstraintsTotalWeight[entry.getValue()], entry.getValue(), 6);
            }

        }
    }
}
