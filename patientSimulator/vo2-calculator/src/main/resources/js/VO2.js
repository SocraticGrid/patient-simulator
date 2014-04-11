
var resultVCO2;
var resultTN2;
var CO;
var capABG=new Array();	// save final values for output

var txtVO2 = 0;
var txtVCO2 = 0;
var txtCO = 0;
var txtHgb = 0;
var txtTemp = 0;
var txtPb = 0;
var txtFiO2 = 0;

var SliderArrays = new Array();
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());
SliderArrays.push(new Array());

var results = new Array();
var abgResult;

function compute(){
// main program block

  FiO2=txtFiO2;
  if( FiO2 < 0.05 || FiO2 > 1.0){
	throw "please enter an FiO2 between 0.05 and 1.0";
	return;
  }
  Hgb=txtHgb;
  if( Hgb < 1 || Hgb > 20){
	throw"please enter a Hemoglobin concentration between 1 and 20 g/dl ";
	return;
  }
  CO=txtCO;
  if(CO < 0.1){
	throw"please enter a cardiac output > 0.1 L/min";
	return;
  }
  if(txtVO2 < 50 || txtVCO2 < 50){
	throw"please enter a VO2 & VCO2 > 50 mls/min";
	return;
  }
  Temp=txtTemp;
  if(Temp < 30.0 || Temp > 43.0){
	throw"please enter a temperature between 30 and 43 degrees C";
	return;
  }
  Pb=txtPb;
  if(Pb < 300 || Pb > 1520){
	throw"please enter a barometric pressure between 300 and 1520 mmHg";
	return;
  }

  Pbmw=Pb-VaporPressure(Temp);
  Hct=Hgb*3;
    PiO2=FiO2*Pbmw;
  PvN2=Pbmw-PiO2;
  
  calculate();
  
}

function calculate(){

  var DesiredVO2=txtVO2/CO;
  var DesiredVCO2=txtVCO2/CO;
   
  var VQDist=new Array();
  var QDist=new Array();
  var PVO2=new Array();
  var PVCO2=new Array();
  var PVN2=new Array();
  var VO2Diff=new Array();
  var VCO2Diff=new Array();
  var VN2Diff=new Array();
  
  for(var j=0; j<10; j++){			//set up VQ distribution
    oSliderRow = SliderArrays[j];
    QDist[j] = parseFloat(oSliderRow[3]);
    VQDist[j] = parseFloat(oSliderRow[0]);
  }
  oSliderRow = SliderArrays[10];
  var Deadspace=parseFloat(oSliderRow[3])/100.0;
  
  
  PVO2[0]=40.0;		//setup initial mixed venous guesses
  PVO2[1]=20.0;
  PVCO2[0]=45.0;
  PVCO2[1]=55.0;
  PVN2[0]=PvN2+20.0;
  PVN2[1]=PvN2;

  PvO2=PVO2[0];
  PvCO2=PVCO2[0];
  PvN2=PVN2[0];
  VO2Diff[0]= GetVO2(VQDist,QDist)-DesiredVO2;
  VCO2Diff[0]= resultVCO2-DesiredVCO2;
  VN2Diff[0]= resultTN2;
  
  PvO2=PVO2[1];
  PvCO2=PVCO2[1];
  PvN2=PVN2[1];
  VO2Diff[1]= GetVO2(VQDist,QDist)-DesiredVO2;
  VCO2Diff[1]= resultVCO2-DesiredVCO2;
  VN2Diff[1]=resultTN2;

  var j=1;		//iterate mixed venous point
  while( Math.abs(VO2Diff[j]) > 0.1 || Math.abs(VCO2Diff[j]) > 0.1){
    
      /*  
    if (j > 11){
      throw "The desired VO2 & VCO2 are not possible given the VQ distribution and FiO2.  Try again.\n";
    }
      */
    PVO2[j+1]=PVO2[j]-VO2Diff[j]*(PVO2[j]- PVO2[j-1])/(VO2Diff[j]-VO2Diff[j-1]);
    PVCO2[j+1]=PVCO2[j]-VCO2Diff[j]*(PVCO2[j]- PVCO2[j-1])/(VCO2Diff[j]-VCO2Diff[j-1]);
    PVN2[j+1]=PVN2[j]-VN2Diff[j]*(PVN2[j]- PVN2[j-1])/(VN2Diff[j]-VN2Diff[j-1]);

    j+=1;
    PvO2=PVO2[j];
    PvCO2=PVCO2[j];
    PvN2=PVN2[j];
    try{
        VO2Diff[j]= GetVO2(VQDist,QDist)-DesiredVO2;
        VCO2Diff[j]= resultVCO2-DesiredVCO2;
        VN2Diff[j]=resultTN2;
        
            /*
        print("\n--------------------------\n");
        print("VO2Diff["+j+"]: "+VO2Diff[j]+"\n");     
        print("VCO2Diff["+j+"]: "+VCO2Diff[j]+"\n");     
        print("VN2Diff["+j+"]: "+VN2Diff[j]+"\n");     
        */
    }
    catch(e){
        throw "The desired VO2 & VCO2 are not possible given the VQ distribution and FiO2.  Try again.\n";
    }
  }
  
  PvO2=PVO2[j];
  PvCO2=PVCO2[j];
  PvN2=PVN2[j];
  
  OutputResults(VQDist,QDist,Deadspace,CO);
  
}

function GetVO2(VQDist,QDist){

  MvpH=pH(PvO2,PvCO2);			//calculate mixed venous values
  CvCO2=CaCO2(MvpH,PvCO2,PvO2);
  CvO2=CaO2(MvpH,PvCO2,PvO2);

  var TO2C=0;
  var TCO2C=0;
  var TN2=0;
  for(var j=0; j<10; j++){
    var comp=new Compartment;
    var Q = QDist[j];
    comp.VQ = VQDist[j];
    if(j==0){		//shunt cpt
      TO2C += Q*CvO2;
      TCO2C += Q*CvCO2;
    }else{
      computeABG(comp);
      capABG[j]=comp;		//save for output
      TO2C += Q*comp.CaO2;
      TCO2C += Q*comp.CaCO2;
      TN2 += Q*comp.VN2;
    }
  }
  TCO2C = TCO2C/100.0;
  TO2C = TO2C/100.0;
  resultVCO2=(CvCO2-TCO2C)*10.0;
  resultTN2=TN2/100.0;
  return (TO2C-CvO2)*10.0;
}

function OutputResults(VQDist,QDist,VD,CO){
  MvpH=pH(PvO2,PvCO2);			//calculate mixed venous values
  CvCO2=CaCO2(MvpH,PvCO2,PvO2);
  CvO2=CaO2(MvpH,PvCO2,PvO2);
  
  var TO2C=0;
  var TCO2C=0;
  var sumV=0;
  var MexPO2=0;
  var MexPCO2=0;
  var TN2=0;
  
  for(var j=0; j<10; j++){
    oSliderRow = SliderArrays[j];
    var Q = parseFloat(oSliderRow[3]);
    
    
    var result=new Compartment;
    if(j==0){		//shunt cpt

      
      
      result.VQ       = 0;
      result.Q        = Q.toFixed(1);
      result.pH       = MvpH.toFixed(2);
      result.PO2      = PvO2.toFixed(1);
      result.PCO2     = PvCO2.toFixed(1);
      result.VN2      = Math.abs(PvN2).toFixed(0);
      result.CaO2     = CvO2.toFixed(1);
      result.CaCO2    = CvCO2.toFixed(1);
      result.R        = 0;
      result.AaDO2    = 0;

      TO2C += Q*CvO2;
      TCO2C += Q*CvCO2;
      
    }else{
      comp=capABG[j];		//recover final result
      
      result.VQ       = comp.VQ;
      result.Q        = Q.toFixed(1);
      result.pH       = comp.pH.toFixed(2);
      result.PO2      = comp.PO2.toFixed(1);
      result.PCO2     = comp.PCO2.toFixed(1);
      result.VN2      = Math.abs((Pbmw-comp.PO2 - comp.PCO2)).toFixed(0);
      result.CaO2     = comp.CaO2.toFixed(1);;
      result.CaCO2    = comp.CaCO2.toFixed(1);
      result.R        = comp.R.toFixed(2);
      result.AaDO2    = 0;
      
      TO2C += Q*comp.CaO2;
      TCO2C += Q*comp.CaCO2;
      sumV += Q*comp.VQ;
      MexPO2 += Q*comp.VQ*comp.PO2;
      MexPCO2 += Q*comp.VQ*comp.PCO2;
      TN2+= Q*comp.VN2;
    }
    results.push(result);
  }
  /*
  document.getElementById('Results-Row-11-Col0').innerText='dead space';
  document.getElementById('Results-Row-11-Col1').innerText=0;
  document.getElementById('Results-Row-11-Col2').innerText=' ';
  document.getElementById('Results-Row-11-Col3').innerText=PiO2.toFixed(1);
  document.getElementById('Results-Row-11-Col4').innerText=0;
  document.getElementById('Results-Row-11-Col5').innerText=' ';
  */
 
  TCO2C = TCO2C/100.0;
  TO2C = TO2C/100.0;
  var RT=(CvCO2-TCO2C)/(TO2C-CvO2);
  MexPO2 = MexPO2/sumV;
  MexPCO2 = MexPCO2/sumV;
  sumV = sumV/100.0;

  var VO2=CO*(TO2C-CvO2)*10.0;
  var VCO2=CO*(CvCO2-TCO2C)*10.0;
  
  //document.getElementById('Summary-Row-0-Col0').innerText='overall V/Q = ' + sumV.toFixed(2);
 
  
  var MexpH=pH(MexPO2,MexPCO2);			//calculate ideal values
  var IdealCACO2=CaCO2(MexpH,MexPCO2,MexPO2);
  var IdealCAO2=CaO2(MvpH,MexPCO2,MexPO2);
  var QsQt=100*(IdealCAO2-TO2C)/(IdealCAO2-CvO2);
  
  var abg=new ABG;		// find ABG from contents
  abg=getABGfromContent(abg, MexpH,TO2C,TCO2C);

  				//add in dead-space ventilation
  MexPO2 = MexPO2*(1-VD) + VD*PiO2;
  MexPCO2 = MexPCO2*(1-VD);
  
  var EngDS =100*(abg.pCO2 - MexPCO2)/abg.pCO2;	//Enghoff modification
  var VA = CO * sumV;  // calculate total alveolar ventilation
  var VE =CO*sumV/(1-VD);
  //print(FiO2+" * "+Pbmw+" - "+ abg.pCO2 +" / "+RT+" - "+ abg.pO2+"\n");
  var AaDO2 = FiO2*Pbmw - abg.pCO2/RT - abg.pO2;  //using traditional simplified alveolar-air equation


  abgResult = new Compartment;
  abgResult.VQ       = sumV.toFixed(2);
  abgResult.Q        = 0;
  abgResult.pH       = abg.pH.toFixed(2);
  abgResult.PO2      = Math.round(abg.pO2);
  abgResult.PCO2     = Math.round(abg.pCO2);
  abgResult.VN2      = -1;
  abgResult.CaO2     = -1;
  abgResult.CaCO2    = -1;
  abgResult.R        = RT.toFixed(2);
  abgResult.AaDO2    = AaDO2.toFixed(2);
    
/*
  document.getElementById('Summary-Row-0-Col2').innerText='overall R = ' + RT.toFixed(2);
  document.getElementById('Summary-Row-1-Col0').innerHTML='arterial O<SUB>2</SUB> content = ' + TO2C.toFixed(1)+' ml O<SUB>2</SUB>/100 ml';
  document.getElementById('Summary-Row-2-Col0').innerHTML='arterial CO<SUB>2</SUB> content = ' + TCO2C.toFixed(1)+' ml O<SUB>2</SUB>/100 ml';
  document.getElementById('Summary-Row-3-Col0').innerText=' ' ;
  document.getElementById('Summary-Row-4-Col0').innerHTML='mixed alveolar PO<SUB>2</SUB> = ' + Math.round(MexPO2)+' mmHg';
  document.getElementById('Summary-Row-5-Col0').innerHTML='mixed alveolar PCO<SUB>2</SUB> = ' + Math.round(MexPCO2)+' mmHg';
  document.getElementById('Summary-Row-6-Col0').innerText=' ' ;
  document.getElementById('Summary-Row-7-Col0').innerHTML='VO<SUB>2</SUB> = ' + Math.round(VO2)+' ml O<SUB>2</SUB>/min';
  document.getElementById('Summary-Row-8-Col0').innerHTML='VCO<SUB>2</SUB> = ' + Math.round(VCO2)+' ml CO<SUB>2</SUB>/min';
  document.getElementById('Summary-Row-6-Col2').innerText='Cardiac Output = ' + CO.toFixed(1)+' L/min';
  document.getElementById('Summary-Row-7-Col2').innerText='Minute Ventilation = ' + VE.toFixed(1)+' L/min';
  document.getElementById('Summary-Row-8-Col2').innerText='Alveolar Ventilation = ' + VA.toFixed(1)+' L/min';
  document.getElementById('Summary-Row-9-Col0').innerText=' ' ;
  document.getElementById('Summary-Row-10-Col0').innerText='pH = ' + abg.pH.toFixed(2);
  document.getElementById('Summary-Row-11-Col0').innerHTML='PaO<SUB>2</SUB> = ' + Math.round(abg.pO2)+' mmHg';
  document.getElementById('Summary-Row-12-Col0').innerHTML='PaCO<SUB>2</SUB> = ' + Math.round(abg.pCO2)+' mmHg';
  document.getElementById('Summary-Row-10-Col2').innerHTML='(A-a)DO<SUB>2</SUB> = ' + Math.round(AaDO2) + ' mmHg';
  document.getElementById('Summary-Row-11-Col2').innerText='Physiologic Deadspace = ' + EngDS.toFixed(0)+' %';
  document.getElementById('Summary-Row-12-Col2').innerText='Venous Admixture = ' + QsQt.toFixed(0)+' %';

*/

}  


function getResults(){
    return results;
}

function getAbgResult(){
    return abgResult;
}