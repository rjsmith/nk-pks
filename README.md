nk-pks
======

Demonstration Position Keeping Server application implemented in NetKernel

(c) RSBA Technology Ltd 2014

## Introduction

This repository contains an experimental application of a real-world eFX trading application component, implemented using the Resource-Orientated Computing platform called [NetKernel](http://www.1060research.com/products/).

## Background

I am an independent consulting business analyst and project manager, working in the investment banking industry for over 20 years.  Over the past decade, I have specialised in working on FX e-commerce front-office trading platforms.  I have been responsible for project management, business analysis, architecture, functional design, Java development and extensive manual and automated testing activities.

I was first introduced to Peter Rogers, Tony Butterworth, [NetKernel](http://www.1060research.com/products/) and 1060Research in March 2014 via [Tom Gilb](www.gilb.com).  I was immediately intrigued by the possibilities of Resource-Orientated Computing and resolved to learn more about its capabilities and qualities.

After progressing through a number of the excellent tutorials and videos, I wanted to see how far I can get working on a an application loosely based on a recent real-world application I have worked on.  To help me remember what I have learnt along the way, I also decided to write up my experiences as I progressed in the form of a multi-part diary included in the project.
  
## Contents 

The repository contains several individual NetKernel modules, contained in separate sub-folders.

### urn.uk.co.rsbatechnology.pks

This is the main Position Keeping Server application NK module.  It contains the core position management functionality.  It also included an embedded set of NetKernel documentation, the PKS Diary, which gives an account of the process by which I went about designing and implementing the application.

### urn.test.uk.co.rsbatechnology.co.uk

This NK module contains NetKernel XUnit tests for the PKS application.

## Installation

To manually install these modules into a NetKernel installation, please follow the steps below.  

It is assumed that you already have a working NK5.xSE or EE installation (the installation root folder is referred to as [NKinstall] below), and have already set up a local working copy of this **nk-pks** repository (the relative path from [NKinstall] to the nk-pks iroot folder is referred to as [RelativePathFromNKinstallToPKSinstall] below).

1.  Create a new xml file in the [NKinstall]/etc/modules.d/ folder called **pks.xml**
2.  The contents of pks.xml should refer to the installed location of the modules contained in the nk-pks working copy:

        <modules>
	        <module runlevel="7">[RelativePathFromNKinstallToPKSinstall]/urn.uk.co.rsbatechnology.pks/</module>
	        <module runlevel="7">[RelativePathFromNKinstallToPKSinstall]/urn.test.uk.co.rsbatechnology.pks/</module>
        </modules>

## PKS Diary

Once you have installed the PKS application modules into your NetKernel instance, you should be able to view the embedded PKS Diary book using this link: http://localhost:1060/book/view/book:uk:co:rsbatechnology:pks:diary/

Alternatively, just type “PKS Diary” into the NetKernel Management Console portal search box.


## License

This repository is (c) RSBA Technology Ltd 2014, and licensed under the LGPL v3.0 license, See LICENSE.txt for full text.

