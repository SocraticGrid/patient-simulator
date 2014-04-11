//VQN2.js


//Copyright � Kent S. Kapitan, MD  2005

	var Pb, PiO2,PvN2,Pbmw,Temp,Hct,Hgb,tol,N2Sol;
	var CvO2,CvCO2,PvO2,FiO2,PvCO2,MvpH;

	var A=-8532.2289;		//O2 solubility constants
	var B=2121.401;
	var C=-67.073989;
	var D=935960.87;
	var E=-31346.258;
	var F= 2396.1674;
	var G= -67.104406;

	N2Sol=0.0017;		// mls/100 ml blood @ 37 C
	tol=0.0001;

function Compartment(){
  //constructor for compartment object
  
  this.VQ=0;
  this.pH=0;
  this.PO2=0;
  this.PCO2=0;
  this.CaO2=0;
  this.CaCO2=0;
  this.R=0;
  this.VN2;
  this.Q;
  this.AaDO2;
}

function ABG(){
	//constructor for ABG object
	this.pH=0;
	this.PCO2=0;
	this.PO2=0;
}

function VaporPressure(T){
  var z = (13.96 - 0.0076*T + 0.000012*T*T);
  z=z*(T-100)/(T+273);
  return 760*Math.exp(z);
}

function computeABG(cpt){

  var VQ=cpt.VQ;

  var PO2,PCO2;
  PO2=(PiO2+PvO2)/2;
  
  //print("\n\n1 computeABG() PO2: "+PO2+"\n");
  //print("\n\n2 computeABG() VQ: "+VQ+"\n");
  
  var lastPO2=0;
  var lastPCO2=0;
  var deltaPO2=1;
  var deltaPCO2=1;
  var iter=0;
  
  while((deltaPO2 > 0.001 && deltaPCO2 > 0.001) && iter < 100){

	PCO2=BisectPCO2(PO2,VQ);
	PO2=BisectPO2(PCO2,VQ);
	deltaPO2=PO2-lastPO2;
	deltaPCO2=PCO2-lastPCO2;
	lastPO2=PO2;
	lastPCO2=PCO2;
	iter +=1;
  }
  var PaN2=Pbmw-PO2-PCO2;
  cpt.PO2=PO2;
  cpt.PCO2=PCO2;
  
  //print("\n\n2 computeABG() PO2: "+PO2+" ("+cpt.PO2+")\n");
  //print("\n\n computeABG() PO2: "+PCO2+" ("+cpt.PCO2+")\n");
  cpt.pH=pH(cpt.PO2,cpt.PCO2);
  cpt.CaO2=CaO2(cpt.pH,cpt.PCO2,cpt.PO2);
  cpt.CaCO2=CaCO2(cpt.pH,cpt.PCO2,cpt.PO2);
  var RB=(CvCO2-cpt.CaCO2)/(cpt.CaO2-CvO2);
  cpt.R=RB;
  cpt.VN2=(PaN2-PvN2);

}  

function pH(pO2,pCO2){
  if(pCO2 <= 0)
    pCO2=0.00001;
  
  //print("\n\nPH:\n");
  //print("0 ph("+pO2+","+pCO2+") pH: "+pH+"\n");
  
  try{
    if (!pO2 || !pCO2){
        var v = undefined;
        v.doSomething();
    } 
  } catch(e){
      e.rhinoException.printStackTrace();
  }

  var pH=7.59 - 0.2741*Math.log(pCO2/20.0);
  //print("\n\nPH:\n");
  //print("1 ph("+pO2+","+pCO2+") pH: "+pH+"\n");
  var o2 = O2Sat(pH,pCO2,pO2);
  //print("  ph("+pO2+","+pCO2+") hgb: "+Hgb+"\n");
  //print("  ph("+pO2+","+pCO2+") o2: "+o2+"\n");
  var Y1=0.003 * Hgb * (1 - o2);
  //print("  ph("+pO2+","+pCO2+") Y1: "+Y1+"\n");
  //print("2 ph("+pO2+","+pCO2+") pH: "+pH+"\n");
  pH += Y1;
  //print("3 ph("+pO2+","+pCO2+") pH: "+pH+"\n");
  o2 = O2Sat(pH,pCO2,pO2);
  var Y=0.003 * Hgb * (1 - o2);
  pH = pH - Y1 + Y;
  //print("\n\n");
  return pH;
}

function O2Sat(pH,pCO2,pO2){
  //print("\n\nO2Sat:\n");
  //print("1 O2Sat("+pH+", "+pO2+", "+pCO2+")\n");  
  var exponent= 0.024*(37.0-Temp) + 0.4*(pH-7.40) + 0.0260576*(3.68888-Math.log(Math.abs(pCO2)));
  //print("1 O2Sat("+pH+", "+pO2+", "+pCO2+")exponent: "+exponent+"\n");
  var x=pO2 * Math.pow(10,exponent);
  if(pO2 < 10.0){
    //print("1 O2Sat("+pH+", "+pO2+", "+pCO2+") A\n");    
    //return O2Sat= 0.003683*x + 0.000584*Math.pow(x,2);
    //return 0.003683*x + 0.000584*Math.pow(x,2);
    throw "Error in O2Sat";
  }else{
      //print("1 O2Sat("+pH+", "+pO2+", "+pCO2+") B\n");
    var num=(A*x + B*Math.pow(x,2) + C*Math.pow(x,3) + Math.pow(x,4));
    var den=(D + E*x + F*Math.pow(x,2) + G*Math.pow(x,3) + Math.pow(x,4));
    return num/den;
  }
}

function CaO2(pH,pCO2,pO2){
  return 1.39*Hgb*O2Sat(pH,pCO2,pO2) + 0.003*pO2;
}

function CaCO2(pH,pCO2,pO2){
  var K2=7.4-pH;
  var pK=6.086 + 0.042*K2 + (38.0-Temp)*(0.00475 + 0.00139*K2);
  var Sol=0.0307 + 0.00057*(37-Temp) + 0.00002*(Math.pow(37-Temp,2));
  var CP=Sol * pCO2 * (1 + Math.pow(10,pH-pK));
  var DOX=0.59 + 0.2913*K2 - 0.0844*Math.pow(K2,2);
  var DR=0.664 + 0.2275*K2 - 0.0938*Math.pow(K2,2);
  var DDD=DOX + (DR-DOX)*(1-O2Sat(pH,pCO2,pO2));
  var CCC=DDD*CP;
  
  return (Hct * 0.01*CCC + (1 - Hct*0.01)*CP)*2.22;
}

function VQDiff_CO2(pO2,pCO2,VQ){
  var PH=pH(pO2,pCO2);
  var VQ1 = 8.63*(CvCO2 - CaCO2(PH,pCO2,pO2))/pCO2;
  return VQ1-VQ;
}

function VQDiff_O2(pO2,pCO2,VQ){
		//includes N2 exchange
		
  var PaN2=Pbmw-pCO2-pO2;
  var FaN2=PaN2/Pbmw;
  var FiN2=1.0-FiO2;
  var PH=pH(pO2,pCO2);
  
  var ViQ=N2Sol*(PaN2-PvN2) + VQ*FaN2;
	  ViQ=ViQ/FiN2;
  var VQ1 = ViQ*FiO2*Pbmw - 8.63*(CaO2(PH,pCO2,pO2)-CvO2);
	  VQ1=VQ1/pO2;
	  
  return VQ1-VQ;
}

function VIQ(pO2,pCO2,VQ){
  var PaN2=Pbmw-pCO2-pO2;
  var FaN2=PaN2/Pbmw;
  var FiN2=1.0-FiO2;
  
  var ViQ=N2Sol*(PaN2-PvN2) + VQ*FaN2;
	  
  return ViQ/FiN2;

}


function BisectCO2C(PCO21,PCO22,pH,pO2,TotCO2){
  var a=PCO21;
  var b=PCO22;
  var c=(a+b)/2;

  var FB=CaCO2(pH,b,pO2)-TotCO2;
  
  while( Math.abs(b-c)> 0.05){
   
    var FC=CaCO2(pH,c,pO2)-TotCO2;
  
    if( sign(FB)*sign(FC) <=0){
      a=c;
    }else{
      b=c;
      FB=CaCO2(pH,b,pO2)-TotCO2;
    }
    c=(a+b)/2;
  }
  return c;
}

function BisectO2C(PO21,PO22,pH,pCO2,O2C){
  var a=PO21;
  var b=PO22;
  var c=(a+b)/2;
  
  var FB=CaO2(pH,pCO2,b)-O2C;
  while( Math.abs(b-c)> 0.05){
   
    var FC=CaO2(pH,pCO2,c)-O2C;
    
    if( sign(FB)*sign(FC) <=0){ //root bracketed between B & C
        a=c;
    }else{
        b=c;
		FB=CaO2(pH,pCO2,b)-O2C;
	}
    c=(a+b)/2;
  }
  return c;
}

function BisectPCO2(pO2,VQ){
  var a=PvCO2+5.0;
  var b=0.1;
  var c=(a+b)/2;
  
  //print("\n\nBisectPCO2:\n");
  //print("1 BisectPCO2("+pO2+", "+VQ+") c: "+c+"\n");  
  
  var FB=VQDiff_CO2(pO2,b,VQ);
  while( Math.abs(b-c)> tol){
   
    var FC=VQDiff_CO2(pO2,c,VQ);
  
    if( sign(FB)*sign(FC) <=0){
      a=c;
    }else{
      b=c;
      FB=VQDiff_CO2(pO2,b,VQ);
    }
    c=(a+b)/2;
  }
  return c;
}

function BisectPO2(pCO2,VQ){
  var a=PvO2;
  var b=PiO2;
  var c=(a+b)/2;
  
  //print("\n\nBisectPO2:\n");
  //print("1 BisectPO2("+pCO2+", "+VQ+") c: "+c+"\n");  
  
  var FB=VQDiff_O2(b,pCO2,VQ);
  while( Math.abs(b-c)> tol){
   
    var FC=VQDiff_O2(c,pCO2,VQ);
  
    if( sign(FB)*sign(FC) <=0){
      a=c;
    }else{
      b=c;
      FB=VQDiff_O2(b,pCO2,VQ);
    }
    c=(a+b)/2;
  }
  return c;
}


function getABGfromContent(abg,PH,O2Content,CO2Content){

  var oldPO2=BisectO2C(PvO2,PiO2,PH,40,O2Content);
  var oldPCO2=BisectCO2C(0,PvCO2,PH,oldPO2,CO2Content);
  var NewPH=pH(oldPO2,oldPCO2);
  var dPO2=oldPO2;
  var dPCO2=oldPCO2;
  var NewPO2;
  var NewPCO2;
  
  while(Math.abs(dPO2)>0.1 && Math.abs(dPCO2) > 0.1){
	NewPO2=BisectO2C(PvO2,PiO2,NewPH,oldPCO2,O2Content);		
	NewPCO2=BisectCO2C(0,PvCO2,NewPH,oldPO2,CO2Content);
	dPO2=NewPO2-oldPO2;
	dPCO2=NewPCO2-oldPCO2;
	oldPO2=NewPO2;
	oldPCO2=NewPCO2; 
	NewPH=pH(oldPO2,oldPCO2);
  }
  abg.pH=NewPH;
  abg.pCO2=NewPCO2;
  abg.pO2=NewPO2;
  
  return abg;
}

function sign(x){
  if(x<0)
    return -1;
  else
    return 1;
}


