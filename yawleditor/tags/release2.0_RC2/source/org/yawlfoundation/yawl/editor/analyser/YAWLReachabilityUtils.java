/*
 * Created on 16/02/2006
 * YAWLEditor v1.4 
 *
 * @author Moe Thandar Wyn
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.yawlfoundation.yawl.editor.analyser;

import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.exceptions.*;
import org.yawlfoundation.yawl.elements.state.*;
import org.yawlfoundation.yawl.elements.YNet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class is used to determine reachability set for a YAWL net.
 *
 */
public class YAWLReachabilityUtils{
     YSetOfMarkings endMarkings = new YSetOfMarkings();
     YSetOfMarkings VisitedMarkings = new YSetOfMarkings();
     int maxNumMarkings = 10000;
     YNet _yNet;
     YSetOfMarkings RS;
     Set firedTasks = new HashSet(100); 
     Set orJoins = new HashSet(10);
     Map ojMarkingsMap = new HashMap(10);
     
     public YAWLReachabilityUtils(YNet net){
      _yNet =  transformNet(net);

     }

     private AnalysisDialog messageDlg;

     public void setAnalysisDialog(AnalysisDialog dlg) {
         messageDlg = dlg;
     }
     
     /**
      * It returns whether the set of markings contains markings that marks
      * all input places of OJ and not smaller AND-join - 0
      * only one input place of OJ and not bigger 1
      * more than one input place - 3 and 4
      */
     private int checkORjoinStatus(YSetOfMarkings RS,Set presetOJ)
     { 
      List  preSetList = new LinkedList(presetOJ);
       YMarking orJoinANDMarking = new YMarking(preSetList);
       YSetOfMarkings orJoinXORMarkings = new YSetOfMarkings();
       for(Iterator ip = presetOJ.iterator(); ip.hasNext();)
       {  YExternalNetElement e = (YExternalNetElement) ip.next();
          List eList = new LinkedList();
          eList.add(e);
          YMarking m = new YMarking(eList);
          orJoinXORMarkings.addMarking(m);
       } 
       
       //check if RS contains all marked preset and not smaller
       if (RS.containsBiggerEqual(orJoinANDMarking)){ 
         if (!containsLessThanMarking(RS,orJoinANDMarking,presetOJ)) {
           return 0;
         }
         return 3;
       }
        //otherwise check one each
        for (Iterator i=orJoinXORMarkings.getMarkings().iterator();i.hasNext();)
        {   YMarking orJoinXORMarking = (YMarking) i.next();
        	  boolean isXOR = checkXOR(RS,orJoinXORMarking,presetOJ);
        	  if (!isXOR)
        	  {
        		return 4;
        	  }
        }
        return 1;
     } 
     
     /**
      * returns true if there is a smaller marking M[preset] < orJoinANDMarking.
      *
      */
       private boolean containsLessThanMarking(YSetOfMarkings RS, YMarking orJoinANDMarking,Set presetOJ)
       {
      
       for(Iterator i = RS.getMarkings().iterator(); i.hasNext();)
       { YMarking m = (YMarking) i.next();
         List locations = new LinkedList(m.getLocations());
         locations.retainAll(presetOJ);
         //only for markings that mark preset of OJ.
         if (locations.size() >0)
         { 
         YMarking mp = new YMarking(locations);
         if (!mp.isBiggerThanOrEqual(orJoinANDMarking))
	       	 { return true;
	       	 }
       	 } //endif
       } //endfor	 
       return false;
      }

     /**
      * returns true if no marking bigger than XOR is found in RS.
      */
     private boolean checkXOR(YSetOfMarkings RS,YMarking orJoinXORMarking,Set presetOJ)
       {
     
       for(Iterator i = RS.getMarkings().iterator(); i.hasNext();)
       { YMarking m = (YMarking) i.next();
         List locations = m.getLocations();
         locations.retainAll(presetOJ);
         YMarking mp = new YMarking(locations);
         if (mp.isBiggerThan(orJoinXORMarking))
       	 { return false;
       	 }
       }
       return true;
      }
      
     /**
      * Used to detect whether OR-joins should be replaced by XOR or AND.
      * returns a message.
      *
      */ 
   	 public String checkUnnecessaryORJoins() throws Exception
	 {  String msg="";
	    String xor="";
	    String and="";
	    boolean changeToAND = false;
	    boolean changeToXOR = false;
	    if (RS == null)
	    {
	    List iLocation = new LinkedList();
        iLocation.add(_yNet.getInputCondition());
        YMarking Mi = new YMarking(iLocation); 
        
        RS = getReachableMarkings(Mi);
        }
        
        //first identify all OR-joins
        for(Iterator i = orJoins.iterator(); i.hasNext();)
        { YTask orJoin = (YTask) i.next();
          Set   preSet = orJoin.getPresetElements();
          YSetOfMarkings ojMarkings = (YSetOfMarkings) ojMarkingsMap.get(orJoin.getID());
          if (ojMarkings != null)
          {
          int  status = checkORjoinStatus(ojMarkings,preSet);
          if (status == 0)
          { changeToAND = true;
            and += orJoin.getID()+" ";
          }
          else if (status == 1)
          { changeToXOR = true;
            xor += orJoin.getID()+" ";
          }
          } 
        }
        
         
       if(changeToAND)
       {
       	 xor = "OR-join task(s) " + and +" in the net "+_yNet.getID() 
       	      +" could be modelled as AND-join tasks."; 
       	 msg = formatXMLMessage(xor,true);    
       } 
       if (changeToXOR)
       {
       	 and += "OR-join task(s) " + xor +" in the net "+_yNet.getID() 
       	      +" could be modelled as XOR-join tasks."; 
       	 msg += formatXMLMessage(and,true); 
       }
       if (!changeToAND && !changeToXOR)
       { msg = "The net "+_yNet.getID()+" satisfies the immutable OR-joins property.";
         msg = formatXMLMessage(msg,true); 
	    } 
	     
       
	   return msg;	
	   
	 }
     /* Not used in the code
     public String checkWeakSoundness() throws Exception
      {
      	
        String msg = "";  
        
        List iLocation = new LinkedList();
        iLocation.add(_yNet.getInputCondition());
        List oLocation = new LinkedList();
        oLocation.add(_yNet.getOutputCondition());
	   	
	   	YMarking Mi = new YMarking(iLocation); 
	   	YMarking Mo = new YMarking(oLocation); 	
	   	
	   	boolean optionToComplete = true;
	   	boolean properCompletion = true;
	   	boolean noDeadTasks = true;
	   	
	   	RS = getReachableMarkings(Mi);
	    
       String omsg;
        //To check whether Mo>=o is reachable / coverable.
   	   	if (Coverable(RS,Mo))
   	   	{ omsg = "The net "+_yNet.getID()+" has weak option to complete.";
	    }
   	    else
   	    { omsg = "The net "+_yNet.getID()+" does not have weak option to complete.";
   	      optionToComplete = false;
   	    }
   	 
   	  msg += formatXMLMessage(omsg,optionToComplete);
   	  
   	  
   	  //Check the end markings for other end markings. 
   	  String pmsg = "";
   	  for (Iterator i = endMarkings.getMarkings().iterator(); i.hasNext();)
	     {
	       YMarking currentM = (YMarking) i.next();	
	       if (currentM.isBiggerThan(Mo))
	       { pmsg +="The net "+_yNet.getID()+" does not have proper completion. A marking "+printMarking(currentM)+"larger than the final marking is coverable." ;
	         properCompletion = false;
	       }
	      
	     }
	
	   if (pmsg.equals(""))
	   {
	   	pmsg = "The net has proper completion.";
	   }
	   else
       { msg += formatXMLMessage(pmsg,properCompletion); 
       }
	   
	   
	   String dmsg = "";
	    for (Iterator i = _yNet.getNetElements().values().iterator(); i.hasNext();)
	     { 
	      YExternalNetElement nextElement = (YExternalNetElement) i.next();
	       
	       if(nextElement instanceof YTask)
	       { 
		       //Need to check join type
		       YTask t = (YTask) nextElement;
		       Set preSet = t.getPresetElements();
		       List preList = new Vector(preSet);
		       
		       if (t.getJoinType() == YTask._AND)
		       {
		       	YMarking currentM = new YMarking(preList);
		        if (!Coverable(RS,currentM))
		   	    {
		         dmsg += t.getID()+" ";
		   	   	 noDeadTasks = false;
		   	    }
		       } //XOR and OR
		       else
		       { boolean dead=true;
		       	for (Iterator prei = preSet.iterator(); prei.hasNext();)
		       	{ YCondition condition = (YCondition ) prei.next();
		       	  preList = new Vector();
		       	  preList.add(condition);
		       	  YMarking currentM = new YMarking(preList);
		          if (Coverable(RS,currentM))
		   	      {
		            dead = false;
		           
		          } 
		        }  
		         if (dead)
		         {
		         	dmsg += t.getID()+" ";
		   	   	    noDeadTasks = false;
		         } 
		         	
		       	
		       }//end else
	        	
	   	  }//end if task
	    
	  }//end for
	  if (dmsg.equals(""))
	    { dmsg = "The net "+_yNet.getID()+" has no dead tasks.";
	    } 
	    else
	    { dmsg = "The net "+_yNet.getID()+" has dead tasks:" + dmsg;
	    }
	     
	   msg += formatXMLMessage(dmsg,noDeadTasks);
	   	   
	   
	   //To display message regarding weak soundness property.
	   String smsg;
	   boolean isWeakSound = true;
	   if (optionToComplete && properCompletion && noDeadTasks)
	   { 
	      smsg = "The net "+_yNet.getID() +" satisfies the weak soundness property.";
	   }
	   else
	   { smsg = "The net "+_yNet.getID() +" does not satisfy the weak soundness property.";
	     isWeakSound = false; 
	   }
	  msg += formatXMLMessage(smsg,isWeakSound);
   	  return msg;
   	 
      }
*/
     /**
    *  To check if the cancellation sets are unnecessary.
    *
    */
     public String checkCancellationSets() throws Exception
   { //YNet originalNet = (YNet)_yNet.clone();
     //_yNet =  transformNet(_yNet);
     
     List iLocation = new LinkedList();
     iLocation.add(_yNet.getInputCondition());	
	 YMarking Mi = new YMarking(iLocation); 
	 YSetOfMarkings RS = getReachableMarkings(Mi);
	 List mLocation = new Vector();
	 YMarking M; 	 
  
   	 Set removeSet = new HashSet();
     ArrayList msgArray = new ArrayList();
     String msg= "";
    //Check cancellation set.
   
   	boolean tokenExists = false;
   	for (Iterator i = _yNet.getNetElements().values().iterator(); i.hasNext();)
   	{ 
   		YExternalNetElement e = (YExternalNetElement) i.next();
     
	     if (e != null && e instanceof YTask)
	   	 { YTask t = (YTask) e;
     
	     	if (!t.getRemoveSet().isEmpty())
	     	{     Set preSet = t.getPresetElements();
		        //Object[] array = preSet.toArray();
		        //Assume there is only one place
		        //RPlace p = (RPlace) array[0];
		        mLocation.clear();
		        for (Iterator pi = preSet.iterator();pi.hasNext();)
		        {   YExternalNetElement ec = (YExternalNetElement) pi.next();
		        	if (ec instanceof YCondition)
		        	{
		        	 mLocation.add(ec);	
		        	}
        			       	
 				}
   	 	
   	 	//it is possible that input and reset places can overlap
   	 	//now that we can be dealing with reduced nets.
		   	 	removeSet = t.getRemoveSet();
		   	  	for (Iterator ri = removeSet.iterator(); ri.hasNext();)
		   	 	{  YExternalNetElement er = (YExternalNetElement) ri.next();
		   	 	   YExternalNetElement mappedEle = findYawlMapping(er.getID());
		        	if (mappedEle != null && mappedEle instanceof YCondition)
		        	{
		        	 mLocation.add(mappedEle);	
		        	 M = new YMarking(mLocation); 
			       	 tokenExists = Coverable(RS,M);      	 
			       	 if (!tokenExists)
			        { 
			          msgArray.add("Element(s) " + convertToYawlMappings(er) + " should not be in the cancellation set of task(s) "+ convertToYawlMappingsForTasks(t) +"."); 
			        } 
			         mLocation.remove(er);
			        
		        	}
		        	   	 
			      }//endfor
			       
  			} //endif
 		}//end if YTask
	 
	}//endfor        
	    
   if (msgArray.size() == 0) 
   { 
      msg = "The net "+ _yNet.getID() +" satisfies the irreducible cancellation regions property.";
      msg = formatXMLMessage(msg,true);
   }
   else
   {
   	 	for (Iterator mi = msgArray.iterator(); mi.hasNext();)
   	 	{
   	 		String rawmessage = (String) mi.next();
   	 		msg += formatXMLMessage(rawmessage,false);
   	 	}
   }
  // _yNet = originalNet;
   return msg;
   }
   
      private boolean Coverable(YSetOfMarkings RS, YMarking M)
      {
         return RS.containsBiggerEqual(M);
      }
     /**
      * The method to check soundness property of YNets using 
      * reachability analysis.
      * returns messages.
      * note: this method also generates endMarkings, orJoins and orJoinsMarkingsMap
      *
      */
	 public String checkSoundness() throws Exception
	 {
	 	String msg = "";  
        
        List iLocation = new LinkedList();
        iLocation.add(_yNet.getInputCondition());
        List oLocation = new LinkedList();
        oLocation.add(_yNet.getOutputCondition());
	   	
	   	YMarking Mi = new YMarking(iLocation); 
	   	YMarking Mo = new YMarking(oLocation); 	
	   	
	   	boolean optionToComplete = true;
	   	boolean properCompletion = true;
	   	boolean noDeadTasks = true;
	   	
	   	RS = getReachableMarkings(Mi);
	    
	    /*	System.out.println("endMarkings"+endMarkings.size());
	   	
	   	for (Iterator i = RS.getMarkings().iterator(); i.hasNext();)
	    {   YMarking m = (YMarking) i.next();
	      	System.out.println(printMarking(m));
	   	}
	*/
	     String omsg;

        //To check whether exact marking Mo=o is reachable.
   	   	if (RS.contains(Mo))
   	   	{ omsg = "The net "+_yNet.getID()+" has an option to complete.";
	    }
   	    else
   	    { omsg = "The net "+_yNet.getID()+" does not have an option to complete. The final marking is not reachable.";
   	      optionToComplete = false;
   	    }

     for (Iterator i = endMarkings.getMarkings().iterator(); i.hasNext();)
  	  { YMarking currentM = (YMarking) i.next();
  	  	if (!currentM.equivalentTo(Mo))
  	    { omsg +="The net "+_yNet.getID()+" can deadlock at marking:"+ printMarking(currentM);
  	         optionToComplete = false;
  	    }
  	  }
   	  msg += formatXMLMessage(omsg,optionToComplete);
   	  
   	  

     // Check for larger end markings than o.
 	  String pmsg = "";

   	  for (Iterator i = RS.getMarkings().iterator(); i.hasNext();)
   	  {
   		YMarking currentM = (YMarking) i.next();
   		if (currentM.isBiggerThan(Mo))
   		{ pmsg +="A marking "+printMarking(currentM)+" larger than the final marking is reachable." ;
   		  properCompletion = false;
   		}
   	  }

	   if (pmsg.equals(""))
	   {
	   	pmsg = "The net "+_yNet.getID()+" has proper completion.";
	   }
	   else

       {   pmsg = "The net "+_yNet.getID()+" does not have proper completion. " + pmsg;

	   }
		   msg += formatXMLMessage(pmsg,properCompletion);

	   String dmsg = "";
	    for (Iterator i = _yNet.getNetElements().values().iterator(); i.hasNext();)
	     { 
	      YExternalNetElement nextElement = (YExternalNetElement) i.next();
	       
	       if(nextElement instanceof YTask)
	       { 
	       
	       YTask t = (YTask) nextElement;
	  /*     Set preSet = t.getPresetElements();
	       List preList = new Vector(preSet);
	       YMarking currentM = new YMarking(preList); 	
	   	   if (!RS.containsBiggerEqual(currentM))
	   	   {
	   */   if (!firedTasks.contains(t))
	        {
	       // dmsg += t.getID()+" ";
	          dmsg += convertToYawlMappingsForTasks(t)+", ";
	   	   	noDeadTasks = false;
	   	   }
	      }   
	     } 
	 
	  if (dmsg.equals(""))
	    { dmsg = "The net "+_yNet.getID()+" has no dead tasks.";
	    } 
	    else
	    { dmsg = "The net "+_yNet.getID()+" has dead tasks:" + dmsg;
	    }
	     
	   msg += formatXMLMessage(dmsg,noDeadTasks);
	   	   
	   
	   //To display message regarding soundness property.
	   String smsg;
	   boolean isSound = true;
	   if (optionToComplete && properCompletion && noDeadTasks)
	   { 
	      smsg = "The net "+_yNet.getID() +" satisfies the soundness property.";
	   }
	   else
	   { smsg = "The net "+_yNet.getID() +" does not satisfy the soundness property.";
	     isSound = false; 
	   }
	  msg += formatXMLMessage(smsg,isSound);
	  
	 return msg;
   	 }
   	 
   	private YSetOfMarkings getReachableMarkings(YMarking M) throws Exception{
	
	YSetOfMarkings RS = new YSetOfMarkings();
	VisitedMarkings = new YSetOfMarkings();

    YSetOfMarkings visitingPS = getImmediateSuccessors(M);
    RS.addMarking(M);
    VisitedMarkings.addMarking(M);

    while (!RS.containsAll(visitingPS.getMarkings()))
       { 
         RS.addAll(visitingPS);
            if(RS.size() > maxNumMarkings)
        {
    		throw new Exception("Reachable markings > "+maxNumMarkings+ ". Possible infinite loop in the net "+_yNet.getID());
        }

    	YSetOfMarkings successors = getImmediateSuccessors(visitingPS);
        visitingPS.removeAll();
        visitingPS.addAll(successors);
        messageDlg.write("Immediate Successors: "+ visitingPS.size());

        }

     messageDlg.write("Reachability Set size: "+RS.size());
     return RS; 
    
    }
	
    /**
     * return successor markings from a set of markings.
     *
     *
     */
    private YSetOfMarkings getImmediateSuccessors(YSetOfMarkings markings)
    {
    YSetOfMarkings successors = new YSetOfMarkings();
    for (Iterator i = markings.getMarkings().iterator(); i.hasNext();)
    {
       YMarking currentM = (YMarking) i.next();	

       if (!VisitedMarkings.contains(currentM))
       {
       YSetOfMarkings post = getImmediateSuccessors(currentM);
       VisitedMarkings.addMarking(currentM);
       if(post.size() > 0)
       { successors.addAll(post);
       }
       else
      {
      	endMarkings.addMarking(currentM);
      }	
     }  
       else
       {
    	  // System.out.println("In visited markings: " + currentM);

       }
     }
    return successors;
    }
    
    
    /**
     * return successor markings from a marking.
     *
     *
     */
    private YSetOfMarkings getImmediateSuccessors(YMarking currentM)
    {
    YSetOfMarkings successors = new YSetOfMarkings();
    for (Iterator i = _yNet.getNetElements().values().iterator(); i.hasNext();)
    { YExternalNetElement ele = (YExternalNetElement) i.next();
      if (ele instanceof YTask)  
      {  YTask t = (YTask) ele;
      	if (isForwardEnabled(currentM,t))

	     {  YSetOfMarkings nextMs = getNextMarkings(currentM,t);

	     /*   System.out.println("Reachable markings for M " + printMarking(currentM) + " for transition " + t.getID());
	     	for (Iterator it = nextMs.getMarkings().iterator(); it.hasNext();)
		    {   YMarking m = (YMarking) it.next();
		      	System.out.println(printMarking(m));
		   	}
		 */
			successors.addAll(nextMs);
	        firedTasks.add(t);
	     }
      }
    } 
    return successors;
   }
    
    /***
     * Changes made to fix the bug with XORsplit
     *
     */
     private YSetOfMarkings getNextMarkings(YMarking currentM,YTask t)
     { YSetOfMarkings successors = new YSetOfMarkings();
       Set preset = new HashSet(t.getPresetElements());
       Set postset = new HashSet(t.getPostsetElements());
       Set cancelset = new HashSet(t.getRemoveSet());
       
       int splitType = t.getSplitType();
       int joinType =  t.getJoinType();
       List locations = new LinkedList(currentM.getLocations());
       switch(joinType){
       	case YTask._AND:
       	{   
       	    for(Iterator i= preset.iterator();i.hasNext();)
       	    { YExternalNetElement ele = (YExternalNetElement)i.next();
       	      locations.remove(ele);
       	  	}
       	  	YMarking M = new YMarking(locations);
       	  	successors.addMarking(M);
       	}
       	break;
       	case YTask._OR:
       	{
       	    for(Iterator i=preset.iterator();i.hasNext();)
       	    { YExternalNetElement ele = (YExternalNetElement) i.next();
       	      locations.remove(ele);
       	  	}
       	  	YMarking M = new YMarking(locations);
       	  	successors.addMarking(M);
       	}
       	break;
       	case YTask._XOR:
       	{
       	    for(Iterator i=preset.iterator();i.hasNext();)
       	    { YExternalNetElement ele = (YExternalNetElement)i.next();
       	      //if it is marked
       	      if (locations.contains(ele))
       	      { locations.remove(ele);
       	        YMarking M = new YMarking(locations);
       	        successors.addMarking(M);
       	        locations.add(ele);
       	      }
            }
       	}
       	break;
       }
    //  System.out.println("No.of Successors. after join for task"+t.getID()+successors.size());
       
       //Remove tokens from cancellation region
       if (cancelset.size()>0)
       { YSetOfMarkings temp = new YSetOfMarkings();
         for (Iterator c = successors.getMarkings().iterator(); c.hasNext();)
    	 {
       		YMarking M = (YMarking) c.next();
       		List slocations = new LinkedList(M.getLocations());
       		for(Iterator i= cancelset.iterator();i.hasNext();)

       	    {
       		  YExternalNetElement ele = (YExternalNetElement)i.next();
       		  slocations.remove(ele);

       	  	}
       	  	YMarking Mt = new YMarking(slocations);
       	  	temp.addMarking(Mt);	
       
         }
       successors.removeAll();
       successors.addAll(temp);
       }
   //    System.out.println("No.of Successors. after cancel for task"+t.getID()+successors.size());
       

        switch(splitType){
       	case YTask._AND:
       	{     	  	    	
       	  YSetOfMarkings temp = new YSetOfMarkings();
          for (Iterator i = successors.getMarkings().iterator(); i.hasNext();)
    	  {
       		YMarking M = (YMarking) i.next();
       		List slocations = new LinkedList(M.getLocations());
       		for(Iterator ip = postset.iterator();ip.hasNext();)
       	    { YExternalNetElement ele = (YExternalNetElement) ip.next();
       	      slocations.add(ele);
       	  	}
       	  	YMarking Mt = new YMarking(slocations);
       	  	temp.addMarking(Mt);	
       
         }
       	successors.removeAll();
       	successors.addAll(temp);
       	
       	}
       	break;
       	case YTask._OR:
       	{ //generate combinations
       	  Set xSubSet = new HashSet();
	      for (int i=1; i <= postset.size(); i++)
	      {  Set subSet = generateCombination(postset,i);
	         xSubSet.addAll(subSet);
	      }
		      
	      YSetOfMarkings temp = new YSetOfMarkings();
	       for (Iterator i = successors.getMarkings().iterator(); i.hasNext();)
	       {  YMarking M = (YMarking) i.next();
	       	 for(Iterator s = xSubSet.iterator(); s.hasNext();)
	         {  Set subSet = (HashSet) s.next();
	            List slocations = new LinkedList(M.getLocations());
	            slocations.addAll(subSet);
	            YMarking Mt = new YMarking(slocations);
	           temp.addMarking(Mt);
       	      } //end for xsubset
       	   } //end for successors
       	 
	      successors.removeAll();
	      successors.addAll(temp);    		
       	}
       	
       	break;
       	case YTask._XOR:
       	{
       	  YSetOfMarkings temp = new YSetOfMarkings();
          for (Iterator i = successors.getMarkings().iterator(); i.hasNext();)
    	  {
       		YMarking M = (YMarking) i.next();
       	    List slocations = new LinkedList(M.getLocations());
       		
       		for(Iterator ip= postset.iterator();ip.hasNext();)
       	    { YExternalNetElement ele = (YExternalNetElement) ip.next();
       	      slocations.add(ele);
       	      YMarking Mt = new YMarking(slocations);
       	  	  temp.addMarking(Mt);
       	      slocations.remove(ele);
       	  	}
        }
       	successors.removeAll();
       	successors.addAll(temp);
       	}
       	break;
       }
   
   //  System.out.println("No.of Successors. after split for task"+t.getID()+successors.size());
     return successors;
     	
     	
     }
      
  
     private boolean isForwardEnabled(YMarking currentM, YTask t)
    {   Set preSet = t.getPresetElements();
          
        int joinType = t.getJoinType();
          switch (joinType) {
            case YTask._AND:
                {   List eleList = new LinkedList();
                 	eleList.addAll(preSet);
                	YMarking m = new YMarking(eleList);
                	return currentM.isBiggerThanOrEqual(m);
                }	 
            case YTask._OR:
                {   //use for unnecessary OR-joins
                    orJoins.add(t); 
                    YIdentifier id = convertMarkingToIdentifier(currentM);
                    boolean isOJEnabled = _yNet.orJoinEnabled(t,id);
                    //use for unnecessary OR-join enabling markings.
                    if (isOJEnabled) 
                    { 
                      YSetOfMarkings ojMarkings = (YSetOfMarkings) ojMarkingsMap.get(t.getID());
                      if (ojMarkings == null)
                      { ojMarkings = new YSetOfMarkings();
                      }
                      ojMarkings.addMarking(currentM);
                      ojMarkingsMap.put(t.getID(),ojMarkings);
                    }
                    return isOJEnabled;
                }
            case YTask._XOR:
                for (Iterator i = preSet.iterator(); i.hasNext();) {
                	YExternalNetElement ele = (YExternalNetElement) i.next();
                	List eleList = new LinkedList();
                	eleList.add(ele);
                	YMarking m = new YMarking(eleList);
                	if (currentM.isBiggerThanOrEqual(m)) {
                	
                        return true;
                    }
                }
                return false;
            default:
                return false;
            }
   
    }

  /**
     * This method is used to generate combinations of markings for 
     * comparison. 
     */
   	private Set generateCombination(Set netElements,int size){
    
    Set subSets = new HashSet();
	Object[] elements = netElements.toArray();
	int[] indices;
	CombinationGenerator x = new CombinationGenerator(elements.length, size);
    while (x.hasMore ()) {
	  Set combsubSet = new HashSet();
	  indices = x.getNext ();
	  for (int i = 0; i < indices.length; i++) { 
	    combsubSet.add(elements[indices[i]]);
	  }
	  subSets.add(combsubSet);
	}
	return subSets;
} 

	/**
	 * used for formatting xml messages.
	 * Message could be a warning or observation. 
	 */ 
     private String formatXMLMessage(String msg,boolean isObservation)
	 { 
	  
	   String xmlHeader;
	   String xmlFooter;
	  if (isObservation)	
	  {
	      xmlHeader="<observation>";
		  xmlFooter="</observation>";
		  
	  }
	  else
	  {
		  xmlHeader="<warning>";
		  xmlFooter="</warning>";
	  } 	 
	  	return xmlHeader + msg + xmlFooter;
	 	
	 }
	 
	 
	 //A convenient method to convert a marking that we have into an identifer object.
	 private YIdentifier convertMarkingToIdentifier(YMarking m)
	 {
	 	YIdentifier id = new YIdentifier("temp");
	 	List locations = new LinkedList(m.getLocations());
	 	
	 	try{
	 		for (Iterator i= locations.iterator();i.hasNext();)
		{  YExternalNetElement e = (YExternalNetElement) i.next();
	   	   if (e instanceof YTask)
	   	   {  YTask t = (YTask) e;
	   	      id.addLocation(null,t);
	   	   }
	       else if (e instanceof YCondition)
	   	   {  YCondition c = (YCondition) e;
	   	      id.addLocation(null,c);
	   	   }
		}
	 	}
	 	catch(YPersistenceException e)
	 	{
	 		//do nothing;
	 		
	 	}
	 
	 	return id;
	 }
	 
	 
	private String printMarking(YMarking m){
  
    String printM = "";
    List mPlaces = m.getLocations();
	for (Iterator i= mPlaces.iterator();i.hasNext();)
	{  YExternalNetElement e = (YExternalNetElement) i.next();
	   printM += e.getID()+"+";
	       
	}
	//To remove the last +
    return printM.substring(0,printM.length()-1);
    }
    
    /**
     * This method is used to transform a YAWL net by spliting 
     * tasks into two and adding a condition in between the two.
     *
     */
    private YNet transformNet(YNet net)
    {


    //Bug fix: It is necessary to first populate the cancelledByTask sets
    // to enable the tasks that are in the cancellation sets to be removed
    // properly during the reachability analysis.
    // An alternative fix will be changing the setRemoveTokensFrom method
    //	in the YTask to call populate the cancelledBy list.
    for (Iterator i= net.getNetElements().values().iterator();i.hasNext();)
	{ YExternalNetElement e = (YExternalNetElement) i.next();
	  if (e instanceof YTask)
	  {
	    YTask t = (YTask) e;
	    Set removeSet = t.getRemoveSet();
	    if (!removeSet.isEmpty())
	    {	 for (Iterator ir = removeSet.iterator();ir.hasNext();)
	  	 {
	  		 YExternalNetElement ele = (YExternalNetElement) ir.next();
	  		 ele.addToCancelledBySet(t);
	  	 }
	    }
	  }
	}
      //for all tasks - split into two

    	for (Iterator i= net.getNetElements().values().iterator();i.hasNext();)
    	{ YExternalNetElement e = (YExternalNetElement) i.next();
    	  if (e instanceof YTask)
    	  { //shortcut
    	    YTask t = (YTask) e;

    	  	YTask t_start = new YAtomicTask(t.getID() +"_start",t.getJoinType(),YTask._AND,net);
    	  	YCondition condition = new YCondition("c_"+t.getID(),"c"+t.getID(),net);
    	  	//introduce a condition in between
    	    //change join behaviour and preset of t_start
    	  	t.setJoinType(YTask._XOR);
    	  	  Set preSet = t.getPresetElements();
    	  	  Iterator preFlowIter = preSet.iterator();
              while (preFlowIter.hasNext())
              { YExternalNetElement next = (YExternalNetElement) preFlowIter.next();
                t_start.setPreset(new YFlow(next,t_start));
                t.removePresetFlow(new YFlow(next,t));
              }
              t_start.setPostset(new YFlow(t_start,condition));
              t.setPreset(new YFlow(condition,t));
              net.addNetElement(t_start);
              net.addNetElement(condition);
              
              Set cancelledBySet = new HashSet(t.getCancelledBySet());
              if (!cancelledBySet.isEmpty()){
              	for (Iterator ic= cancelledBySet.iterator();ic.hasNext();)
              	{ YTask cancelTask = (YTask) ic.next();
              	  cancelTask.removeFromRemoveSet(t);
              	  List newVector = new LinkedList();
              	  newVector.add(condition);
              	  cancelTask.setRemovesTokensFrom(newVector); 
              	}

              }
              	
    	  	} //task
    	  	
    	  } //endfor

    	return net;	
   }
    	
   public static String convertToYawlMappings(YExternalNetElement e){
   String msg = e.getID();
   HashSet mappings = new HashSet(e.getYawlMappings());
   
   for (Iterator i= mappings.iterator();i.hasNext();) 
   {
   	 YExternalNetElement innerEle = (YExternalNetElement)i.next();
   	 mappings.addAll(innerEle.getYawlMappings());
   	 
   }
 
   	msg += "["+ mappings.toString() +"] ";
      
   return msg;	       
   }
    	
   public static String convertToYawlMappingsForConditions(YExternalNetElement e){
   String msg = e.getID();
   HashSet mappings = new HashSet(e.getYawlMappings());
   HashSet condMappings = new HashSet();
   
   for (Iterator in= mappings.iterator();in.hasNext();) 
   {
   	 YExternalNetElement yEle = (YExternalNetElement)in.next();
     if (yEle instanceof YCondition)
     { condMappings.add(yEle);
     } 
   	 
   }
   
   	msg += "["+ condMappings.toString() +"]";
     
   return msg;	       
   }
   
   
   public static String convertToYawlMappingsForTasks(YExternalNetElement e){
   String msg = " ";
   HashSet mappings = new HashSet(e.getYawlMappings());
   HashSet taskMappings = new HashSet();
  
   for (Iterator in= mappings.iterator();in.hasNext();) 
   {
   	 YExternalNetElement yEle = (YExternalNetElement)in.next();
     if (yEle instanceof YTask)
     { taskMappings.add(yEle);
     } 
   	
   }
   if (taskMappings.isEmpty()){
   	msg = e.getID();
   }
   else{
   	msg = "["+ taskMappings.toString() +"]";
   }
  
   return msg;	       
   }
   
   private YExternalNetElement findYawlMapping(String id){
   	
   Map elements = new HashMap(_yNet.getNetElements());
   YExternalNetElement e = (YExternalNetElement) elements.get(id);
   HashSet mappings;
   YExternalNetElement mappedEle,innerEle;
   if (e == null)
   	{ for (Iterator i= elements.values().iterator();i.hasNext();)
   	  {	mappedEle = (YExternalNetElement) i.next();
   	 	mappings = new HashSet(mappedEle.getYawlMappings());
   	 	for (Iterator inner = mappings.iterator();inner.hasNext();)
   	 	{ innerEle = (YExternalNetElement) inner.next();
   	 	 if (innerEle.getID().equals(id))
   	 		{ 	//System.out.println("Mappings from reduced net:"+mappedEle.getID());
   	 	  		return mappedEle;	
   	 		}
   	 	}//inner for
   	 
   	  }	//outer for	 	
   	}
   	else
   	{
   	  //System.out.println("Found in main net:"+e.getID());	
   	  return e;
   	}
   	//System.out.println("Return null from mapping"+id);
   	return null;
   	
   }
 
    	
}	 	
