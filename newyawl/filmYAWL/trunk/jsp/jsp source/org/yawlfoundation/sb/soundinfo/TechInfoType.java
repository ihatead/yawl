//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.06.02 at 12:52:20 PM EST 
//


package org.yawlfoundation.sb.soundinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for techInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="techInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sampleRate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bitRate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeCode_DF" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeCodeSource" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refTone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userBits" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="soundMixer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cameraFrameRate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mediaFormat" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recorder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transferTo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "techInfoType", propOrder = {
    "sampleRate",
    "bitRate",
    "timeCodeDF",
    "timeCodeSource",
    "refTone",
    "userBits",
    "soundMixer",
    "cameraFrameRate",
    "mediaFormat",
    "recorder",
    "transferTo"
})
public class TechInfoType {

    @XmlElement(required = true)
    protected String sampleRate;
    @XmlElement(required = true)
    protected String bitRate;
    @XmlElement(name = "timeCode_DF", required = true)
    protected String timeCodeDF;
    @XmlElement(required = true)
    protected String timeCodeSource;
    @XmlElement(required = true)
    protected String refTone;
    protected boolean userBits;
    @XmlElement(required = true)
    protected String soundMixer;
    @XmlElement(required = true)
    protected String cameraFrameRate;
    @XmlElement(required = true)
    protected String mediaFormat;
    @XmlElement(required = true)
    protected String recorder;
    @XmlElement(required = true)
    protected String transferTo;

    /**
     * Gets the value of the sampleRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the value of the sampleRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleRate(String value) {
        this.sampleRate = value;
    }

    /**
     * Gets the value of the bitRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBitRate() {
        return bitRate;
    }

    /**
     * Sets the value of the bitRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBitRate(String value) {
        this.bitRate = value;
    }

    /**
     * Gets the value of the timeCodeDF property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeCodeDF() {
        return timeCodeDF;
    }

    /**
     * Sets the value of the timeCodeDF property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeCodeDF(String value) {
        this.timeCodeDF = value;
    }

    /**
     * Gets the value of the timeCodeSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeCodeSource() {
        return timeCodeSource;
    }

    /**
     * Sets the value of the timeCodeSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeCodeSource(String value) {
        this.timeCodeSource = value;
    }

    /**
     * Gets the value of the refTone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefTone() {
        return refTone;
    }

    /**
     * Sets the value of the refTone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefTone(String value) {
        this.refTone = value;
    }

    /**
     * Gets the value of the userBits property.
     * 
     */
    public boolean isUserBits() {
        return userBits;
    }

    /**
     * Sets the value of the userBits property.
     * 
     */
    public void setUserBits(boolean value) {
        this.userBits = value;
    }

    /**
     * Gets the value of the soundMixer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoundMixer() {
        return soundMixer;
    }

    /**
     * Sets the value of the soundMixer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoundMixer(String value) {
        this.soundMixer = value;
    }

    /**
     * Gets the value of the cameraFrameRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCameraFrameRate() {
        return cameraFrameRate;
    }

    /**
     * Sets the value of the cameraFrameRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCameraFrameRate(String value) {
        this.cameraFrameRate = value;
    }

    /**
     * Gets the value of the mediaFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediaFormat() {
        return mediaFormat;
    }

    /**
     * Sets the value of the mediaFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediaFormat(String value) {
        this.mediaFormat = value;
    }

    /**
     * Gets the value of the recorder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecorder() {
        return recorder;
    }

    /**
     * Sets the value of the recorder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecorder(String value) {
        this.recorder = value;
    }

    /**
     * Gets the value of the transferTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferTo() {
        return transferTo;
    }

    /**
     * Sets the value of the transferTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferTo(String value) {
        this.transferTo = value;
    }

}