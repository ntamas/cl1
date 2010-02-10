# Splits the rst .html output in various .html files
# Copyright (C) 2009 David Capello
# Modifications by Tamas Nepusz

from xml.dom import Node
from xml.dom.minidom import parse, parseString

import os
import sys

def getElementById(node, id):
    if node.hasAttribute("id") and node.getAttribute("id") == id:
        return node
    for child in node.childNodes:
        if child.nodeType == Node.ELEMENT_NODE:
            found = getElementById(child, id)
            if found:
                return found
    return None

def remapAHref(node, old, new):
    if node.tagName == "a":
        if node.hasAttribute("href") and node.getAttribute("href") == old:
            node.setAttribute("href", new)
        if node.hasAttribute("class"):
            node.removeAttribute("class")
    for child in node.childNodes:
        if child.nodeType == Node.ELEMENT_NODE:
            remapAHref(child, old, new)

def collectIdsInSection(node, ids, section):
    if section:
        if node.hasAttribute("id"):
            ids.append([ section, node.getAttribute("id") ])
    elif node.tagName == "div" and \
            ( node.hasAttribute("class") and node.getAttribute("class") == "section") or \
            ( node.hasAttribute("id") and node.getAttribute("id") == "contents"):
        if node.getAttribute("id") == "contents":
            section = "index"
        else:
            section = node.getAttribute("id")
            ids.append([ section, section ])

    for child in node.childNodes:
        if child.nodeType == Node.ELEMENT_NODE:
            collectIdsInSection(child, ids, section)
    
def splitSection(doc, sectionId, removePrevs, outputFileName, idsToRemap):
    # clone the whole document
    doc2 = doc.cloneNode(True)

    # get the <div> element containing the specified section
    div = getElementById(doc2.documentElement, sectionId)
    if not div:
        return

    # remove previous elements (<div> of previous sections and content table)
    if removePrevs:
        while div.previousSibling:
            div.parentNode.removeChild(div.previousSibling)

    # remove next elements (<div> of next sections)
    while div.nextSibling:
        div.parentNode.removeChild(div.nextSibling)

    file = open(outputFileName, "w")
    # Remove <?xml...?> because the JavaHelp viewer doesn't really like it
    contents = doc2.toxml(encoding="utf-8")
    if contents[0:5] == "<?xml":
        contents = contents[contents.index("\n")+1:]
    file.write(contents)
    file.close()

###################################################################### 

def createJHM(sectionIds, f):
    print >>f, "<!DOCTYPE map"
    print >>f, "  PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN\""
    print >>f, "  \"http://java.sun.com/products/javahelp/map_1_0.dtd\">"
    print >>f
    print >>f, "<map version=\"1.0\">"
    for sectionId in sectionIds:
        print >>f, "  <mapID target=\"%s\" url=\"%s.html\" />" % (sectionId, sectionId)
    print >>f, "</map>"

###################################################################### 

def createTOC(doc, f):
    titles = doc.getElementsByTagName("title")
    if titles:
        titles[0].normalize()
        title = titles[0].firstChild.data
    else:
        title = "Help document"

    print >>f, "<!DOCTYPE toc"
    print >>f, "  PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN\""
    print >>f, "         \"http://java.sun.com/products/javahelp/toc_2_0.dtd\">"
    print >>f, "<toc version=\"2.0\">"
    print >>f, "<tocitem text=\"%s\">" % title
    createTOC_aux(doc, f, "")
    print >>f, "</tocitem>"
    print >>f, "</toc>"

def createTOC_aux(node, f, indent):
    was_created = False

    if hasattr(node, "tagName") and node.tagName == "div" and \
            ( node.hasAttribute("class") and node.getAttribute("class") == "section"):
        title = ""
        for child in node.childNodes:
            if child.nodeType != Node.ELEMENT_NODE: continue
            if child.tagName[0] != "h": continue
            try:
                level = int(child.tagName[1:])
            except:
                continue
            child.normalize()
            title = child.firstChild.data
            break

        print >>f, "%s<tocitem text=\"%s\" target=\"%s\">" % (indent, title, node.getAttribute("id"))
        indent += "  "
        was_created = True

    for child in node.childNodes:
        if child.nodeType == Node.ELEMENT_NODE:
            createTOC_aux(child, f, indent)

    if was_created:
        print >>f, "</tocitem>"
        indent = indent[:-2]

###################################################################### 

infile = sys.argv[1]
manualDoc = parse(infile)
indir = os.path.dirname(os.path.abspath(infile))

# get IDs from any tag with a id="..." attribute
idsToRemap = []
collectIdsInSection(manualDoc.documentElement, idsToRemap, None)

# sections
sectionsIds = []
for id in idsToRemap:
    sectionsIds.append(id[0])
sectionsIds = set(sectionsIds)

# remap sections
for sectionId in sectionsIds:
    remapAHref(manualDoc.documentElement, "#" + sectionId, sectionId + ".html")

# remap all href to the future location
for id in idsToRemap:
    remapAHref(manualDoc.documentElement, "#" + id[1], id[0] + ".html#" + id[1])

# create a file for the content table
# splitSection(manualDoc, "contents", False, "index.html", idsToRemap)

# create a file for each section
for sectionId in sectionsIds:
    splitSection(manualDoc, sectionId, True, os.path.join(indir, sectionId + ".html"), idsToRemap)

# create a JHM file
createJHM(sectionsIds, open(os.path.join(indir, "cl1_map.jhm"), "w"))

# create a TOC file
createTOC(manualDoc, open(os.path.join(indir, "cl1_toc.xml"), "w"))

