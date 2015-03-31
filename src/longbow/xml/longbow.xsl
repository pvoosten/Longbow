<?xml version="1.0" encoding="UTF-8"?>
<!-- A stylesheet that transforms an xml document to a java source file -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"  />
    
    
    <xsl:template match="/workflow">
package <xsl:value-of select="source/@package" />;

// import statements
import longbow.*;
import longbow.core.DefaultLongbowFactory;

import java.util.Map;
import java.util.HashMap;
import longbow.transformations.Getter;
import longbow.transformations.Setter;

public class <xsl:value-of select="source/@class" /> {
        
    private final Workflow workflow;
    private final Map&lt;String, TransformationContext&gt; transformations;
    private final Map&lt;String, Getter&gt; getters;
    private final Map&lt;String, Setter&gt; setters;
    
    // provide access to setters
    <xsl:for-each select="connections/setter">
    private final Setter <xsl:value-of select="@id"/>;
    </xsl:for-each>
    
    // provide access to getters
    <xsl:for-each select="connections/getter">
    private final Getter <xsl:value-of select="@id"/>;
    </xsl:for-each>
        
    public <xsl:value-of select="source/@class" />(){
        <xsl:apply-templates select="factory" />
        workflow = factory.createWorkflow();
        transformations = new HashMap&lt;String, TransformationContext&gt;();
        getters = new HashMap&lt;String, Getter&gt;();
        setters = new HashMap&lt;String, Setter&gt;();
        
        // add transformations to the workflow
        TransformationContext ctx;
        <xsl:for-each select="transformations/transformation">
        ctx = workflow.add(new <xsl:value-of select="@class"/>());
        assert ctx != null : "Couldn't add transformation to workflow";
        transformations.put("<xsl:value-of select="@id"/>", ctx);
        </xsl:for-each>
        // add getters
        boolean connected;
        Metadata metadata;
        TransformationContext ctxGetter;
        <xsl:for-each select="connections/getter">
        ctx = transformations.get("<xsl:value-of select="@context"/>");
        metadata = ctx.getOutput("<xsl:value-of select="@outputId"/>").getMetadata();
        <xsl:value-of select="@id"/> = new Getter(metadata);
        getters.put("<xsl:value-of select="@id"/>", <xsl:value-of select="@id"/>);
        ctxGetter = workflow.add(<xsl:value-of select="@id"/>);
        connected = workflow.connect(ctx, "<xsl:value-of select="@outputId"/>", ctxGetter, Getter.IN_DATA);
        assert connected : "Could not connect getter <xsl:value-of select="@id"/> with output <xsl:value-of select="@outputId"/> of <xsl:value-of select=" @context"/>";
        </xsl:for-each>
        // add setters
        TransformationContext ctxSetter;
        <xsl:for-each select="connections/setter">
        ctx = transformations.get("<xsl:value-of select="@context"/>");
        metadata = ctx.getInput("<xsl:value-of select="@inputId"/>").getMetadata();
        <xsl:value-of select="@id"/> = new Setter(metadata);
        setters.put("<xsl:value-of select="@id"/>", <xsl:value-of select="@id"/>);
        ctxSetter = workflow.add(<xsl:value-of select="@id"/>);
        transformations.put("<xsl:value-of select="@id"/>", ctxSetter); 
        connected = workflow.connect(ctxSetter, Setter.OUT_DATA, ctx, "<xsl:value-of select="@inputId"/>");
        assert connected : "Could not connect setter <xsl:value-of select="@id"/> with input <xsl:value-of select="@inputId"/> of <xsl:value-of select=" @context"/>";
        </xsl:for-each>
        // connect transformations
        TransformationContext from, to;
        <xsl:for-each select="connections/connection">
            assert transformations.containsKey("<xsl:value-of select="@from"/>") : "There is no TransformationContext identified by <xsl:value-of select="@from"/>";
            assert transformations.containsKey("<xsl:value-of select="@to"/>") :  "There is no TransformationContext identified by <xsl:value-of select="@to"/>";
            from = transformations.get("<xsl:value-of select="@from"/>");
            to = transformations.get("<xsl:value-of select="@to"/>");
            assert from != null : "Can't connect null with a context";
            assert to != null : "Can't connect null with a context";
            <xsl:choose>
                <xsl:when test="exists(@inputId) and exists(@outputId)">
                    assert to.getInput("<xsl:value-of select="@inputId"/>").getMetadata().acceptsMetadata(from.getOutput("<xsl:value-of select="@outputId"/>").getMetadata()) : "Metadata is not accepted for connection";
                    assert ! to.getInput("<xsl:value-of select="@inputId"/>").isConnected() : "Input <xsl:value-of select="@inputId"/> is already connected";
                    connected = workflow.connect(from, "<xsl:value-of select="@outputId" />", to, "<xsl:value-of select="@inputId"/>");
                    assert connected : "Failed to connect output <xsl:value-of select="@outputId" /> of <xsl:value-of select="@from" /> with input <xsl:value-of select="@inputId" /> of <xsl:value-of select="@to"/>";
                </xsl:when>
                <xsl:otherwise>
                    connected = workflow.connect(from, to);
                    assert connected : "Failed to connect <xsl:value-of select="@from"/> to <xsl:value-of select="@to" />";
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    }

    public Workflow getWorkflow(){
        return workflow;
    }
    
    public Map&lt;String, TransformationContext&gt; getTransformations(){
        return transformations;
    }
    
    public Setter getSetter(String name){
        return setters.get(name);
    }
    
    public Getter getGetter(String name){
        return getters.get(name);
    }
        
}
    </xsl:template>
    
    
    <xsl:template match="factory">
        final DefaultLongbowFactory factory = new DefaultLongbowFactory();
        <xsl:if test="@workflow">factory.setWorkflowClass("<xsl:value-of select="@workflow"/>");
        </xsl:if><xsl:if test="@input">factory.setInputClass("<xsl:value-of select="@input"/>");
        </xsl:if><xsl:if test="@output">factory.setOutputClass("<xsl:value-of select="@output"/>");
        </xsl:if><xsl:if test="@dataNode">factory.setDataNodeClass("<xsl:value-of select="@dataNode"/>");
        </xsl:if><xsl:if test="@dataWrapper">factory.setDataWrapperClass("<xsl:value-of select="@dataWrapper"/>");
        </xsl:if><xsl:if test="@runner">factory.setRunnerClass("<xsl:value-of select="@runner"/>");
        </xsl:if><xsl:if test="@transformationContext">factory.setTransformationContextClass("<xsl:value-of select="@transformationContext"/>");
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>