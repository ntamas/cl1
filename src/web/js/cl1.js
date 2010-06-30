/***************************************************************************/

/** Form validation routine that accepts positive integers */
jQuery.validator.addMethod("positive_integer", function(value, element) {
  if (this.optional(element))
    return true;
  if (!/^\d+$/.test(value))
    return false;
  return (parseInt(value) >= 1);
}, "Please enter a positive integer");

/** Form validation routine that accepts numbers between 0 and 1 */
jQuery.validator.addMethod("number_01", function(value, element) {
  if (this.optional(element))
    return true;
  if (!/^[0-9.]+$/.test(value))
    return false;
  return (parseFloat(value) >= 0 && parseFloat(value) <= 1);
}, "Please enter a value between 0 and 1 (inclusive)");

/***************************************************************************/

function ClusterONEFrontend() {
  this.datasetUrl = null;
  this.resultUrl = null;
  
  this.currentStep = -1;
  this.currentResults = null;
  this.parameterValidator = null;
  
  this.debugMode = true;
  
  this.init();
}

ClusterONEFrontend.prototype = {
  /** Adds a marker of a given class to the current or given step next to the buttons */
  addMarker: function(message, step, klass) {
    if (step == null)
      step = this.currentStep;

    var $item = $("#steps li").slice(step-1, step);
    
    if ($item.length > 0) {
      var $marker = $("."+klass, $item);
      
      if ($marker.length == 0) {
        $marker = $("<span></span>").addClass(klass);
        $buttons = $(".buttons", $item);
        if ($buttons.length > 0) {
          $(".buttons", $item).slice(0, 1).append($marker);
        } else {
          $(".title", $item).after($marker);
        }
      }
      
      $marker.html(message);
    }
  },
  
  /** Adds a progress marker to the current or given step next to the buttons */
  addProgressMarker: function(message, step) {
    return this.addMarker(message, step, "progress-indicator");
  },
  
  /** Adds a success marker to the current or given step next to the buttons */
  addSuccessMarker: function(message, step) {
    return this.addMarker(message, step, "success-marker");
  },
  
  /** Clears all the error messages */
  clearErrors: function() {
    return this.clearGenericMessages("errors");
  },
  
  /** Clears a generic message container */
  clearGenericMessages: function(container_id) {
    $("#"+container_id).empty();
  },
  
  debug: function(msg) {
    if (!this.debugMode)
      return;
    
    var $item = $("<pre></pre>");
    $item.text(msg);
    $('#debug').append($item);
  },
  
  /** Gets a default AJAX options object that can be used for AJAX requests */
  getDefaultAjaxOptions: function(url, data) {
    var settings = {
      context: this,
      data: data,
      error: this.onAJAXError,
      url: url
    };
    settings.type = data ? 'POST' : 'GET';
    return settings;
  },
    
  /** Initializes the Cluster ONE frontend page */
  init: function() {
    /* Set up the upload button */
    new AjaxUpload('upload-button', {
      action: 'api/dataset',
      name: 'file',
      context: this,
      autoSubmit: true,
      responseType: false,
      onSubmit: function(file, extension) {
        this.addProgressMarker("Please wait...", 1);
      },
      onComplete: this.onFileUploaded
    });
    
    /* Set up the buttons */
    var frontend = this;
    var dispatchClick = function() {
      frontend.onButtonClicked.call(frontend, this.id, this);
    };
    $('#start-button').click(dispatchClick);
    $('#download-button').click(dispatchClick);
    $('#print-button').click(dispatchClick);
    
    /* Set up validation rules for the algorithm parameters form */   
    this.parameterValidator = $("#algorithm_parameters").validate();
    
    /* Set the active step */
    this.setActiveStep(1);
  },
  
  /** Event handler invoked when an error happened during an AJAX call */
  onAJAXError: function(req, status, exc) {
    this.showBug("AJAX call returned with HTTP error code "+req.status+".");
    if (req)
      this.debug($.httpData(req));
    this.removeMarkers();
  },
  
  /** Event handler invoked when the analysis results have arrived */
  onAnalysisCompleted: function(data, status, req) {
    if (req.status != 201) {
      this.showBug("AJAX call returned with HTTP error code "+req.status+".");
      return;
    }
    
    this.resultUrl = req.getResponseHeader('Location');
    if (!this.resultUrl) {
      this.showBug("AJAX call returned no location for the result resource.");
      return;
    }
    
    this.retrieveResults();
  },
  
  /** Event handler invoked when a button was clicked */
  onButtonClicked: function(buttonId, button) {
    if (buttonId == "start-button")
      return this.startAnalysis();
 
    if (buttonId == "print-button") {
      if (this.currentResults)
        this.currentResults.print();
      else
        alert("There are no results yet!");
      return;
    }
    
    if (buttonId == "download-button") {
      if (this.currentResults)
        return this.currentResults.download();
      else
        alert("There are no results yet!");
      return;
    }
    
    alert("This function is not implemented yet!");
  },
    
  /** Event handler invoked when the dataset was uploaded */
  onFileUploaded: function(file, response) {
    this.removeMarkers(1);
    if (response.substr(0, 7) == "http://" ||
        response.substr(0, 8) == "https://") {
      /* Successful file upload, response contains the new URL */
      this.datasetUrl = response;
      this.addSuccessMarker("Dataset successfully uploaded.", 1);
      this.setActiveStep(2);
    } else {
      alert(response);
      /* Error while uploading file */
      this.showError("Error while uploading file. Please try again later.");
    }
  },
  
  /** Removes a marker of a given class from the current or given step */
  removeMarker: function(step, klass) {
    if (step == null)
      step = this.currentStep;
      
    var $item = $("#steps li").slice(step-1, step);
    if ($item.length > 0)
      $("."+klass, $item).remove();
  },
  
  /** Removes all the markers from the page */
  removeMarkers: function(step) {
    if (step == null) {
      $("#steps li .progress-indicator").remove();
      $("#steps li .success-marker").remove();
    } else {
      this.removeMarker(step, "progress-indicator");
      this.removeMarker(step, "success-marker");
    }
  },
  
  /** Retrieves the results from the stored result URL */
  retrieveResults: function() {
    var settings = this.getDefaultAjaxOptions(this.resultUrl);
    settings.success = function(data, status, req) {
      this.removeMarkers(2);
      this.addSuccessMarker("Results successfully retrieved.", 2);
      this.setActiveStep(3);
      this.currentResults = ClusterONEResult.fromJSON(data);
      this.currentResults.render("#results");
    };
    
    // settings.success = this.onResultRetrievalCompleted;
    
    this.addProgressMarker("Please wait, retrieving results...", 2);
    $.ajax(settings);
  },
    
  /** Sets the active step in the frontend */
  setActiveStep: function(activeStep) {
    if (this.currentStep == activeStep)
      return;
      
    var effects = (this.currentStep > 0);
 
    $("#steps li").each(function(index) {
      index = index + 1;
      $this = $(this);
      $this.toggleClass("finished", (activeStep > index));
      $this.toggleClass("active", (activeStep == index));
      
      if (activeStep < index) {
        if (effects && $this.is(":visible")) {
          $this.fadeOut();
        } else {
          $this.hide();
        }
      } else if (activeStep == index) {
        if (effects && !$this.is(":visible")) {
          $this.fadeIn();
        } else {
          $this.show();
        }
      }/* else {
        $contents = $(".contents", $this);
        if (effects && $contents.is(":visible")) {
          $contents.slideUp();
        } else {
          $contents.hide();
        }
      }*/
    });
    
    this.currentStep = activeStep;
  },
  
  /** Shows an error message that is likely to correspond to a bug */
  showBug: function(msg) {
    msg = msg + "<br/><br/>This is likely a bug in Cluster ONE. Please contact the maintainers!";
    return this.showError(msg);
  },
  
  /** Shows an error message in the error container */
  showError: function(msgs) {
    return this.showGenericMessage("errors", msgs);
  },
  
  /** Shows a generic error message in a message container */
  showGenericMessage: function(container_id, msgs) {
    if (typeof(msgs) == "string") {
      msgs = [msgs];
    }
    
    this.clearGenericMessages(container_id);

	var container = $("#"+container_id);
    $.each(msgs, function(index, msg) {
      container.append($("<li></li>").html(msg));
    });
  },
  
  /** Starts the analysis */
  startAnalysis: function() {
    if (!this.datasetUrl) {
      this.showError("Please upload a dataset first!");
      return;
    }
    
    var $form = $("#algorithm_parameters");
    if (!$form.valid()) {
      this.parameterValidator.focusInvalid();
      return;
    } else {
      this.clearErrors();
    }
    
    var slashPos = this.datasetUrl.lastIndexOf("/");
    if (slashPos < 0) {
      this.showBug("Invalid dataset URL.");
      return;
    }
    
    var datasetId = this.datasetUrl.substr(slashPos+1);
    var data = { dataset_id: datasetId };
    var settings = this.getDefaultAjaxOptions("api/result", data);
    settings.success = this.onAnalysisCompleted;
    
    $.each($("#algorithm_parameters").serializeArray(), function(index) {
      data[this.name] = this.value;
    });
    
    this.addProgressMarker("Please wait, running calculations...", 2);
    $.ajax(settings);
  } 
};

/***************************************************************************/

/** Class representing the results of a Cluster ONE run */
function ClusterONEResult() {
  this.clusters = [];
  this.parameters = [];
}

ClusterONEResult.prototype = {
  /** Downloads the results by opening a popup window and rendering the
   * results in there in a simple plain text format
   */
  download: function() {
    var win = this.getPopup("download_window");
    if (!win)
      return;
    
    doc = win.document;
    if (doc.open)
      doc.open("text/plain", true);

    $.each(this.clusters, function(index) {
      doc.write(this.members.join(" "));
      doc.write("\n");
    });
    
    if (doc.close)
      doc.close();
  },
  
  getPopup: function(popupId) {
    var win = window.open(null, popupId);
    if (!win) {
      alert("Could not open popup window. Please disable your popup blocker!");
    }
    return win;
  },
  
  /** Prints the results by opening a popup window, rendering the results
   * in a div in the popup and asking the browser to print the popup.
   */
  print: function() {
    var win = this.getPopup("print_window");
    if (!win) return;

    win.document.write("<html><head>" +
                       "  <title>Cluster ONE results</title>" +
                       "  <link type=\"text/css\" rel=\"stylesheet\" href=\"css/screen.css\" />" +
                       "</head>" +
                       "<body><div id=\"results\"></div></body>" +
                       "</html>");
    if (win.document.close)
      win.document.close();

    this.render($("#results", win.document));
    
    win.print();
  },
  
  /** Renders the results nicely in the given div */
  render: function(id, sorting) {
    var $target, $properties, $item;
    var results = this;
    var numericFields = ["density", "inWeight", "outWeight", "quality"];
    
    $target = $(id);
    $target.empty();
    ownerDocument = $target.get(0).ownerDocument || null;
    
    $properties = $("<dl></dl>", ownerDocument).addClass("compact");
    $properties.append("<dt>Number of clusters:</dt>");
    $properties.append("<dd>"+this.clusters.length+"</dd>");
    $target.append($properties);
    
    var msg = "Please wait, creating table...";
    if ($.browser.msie) {
    	msg += "<br/>This takes a <em>looooong</em> time on Internet Explorer.<br/>Consider using a better browser instead.";
    }
    $progress = $("<p class=\"progress-indicator\">"+msg+"</p>", ownerDocument)
    $target.append($progress);
    
    $table = $("<table cellspacing=\"0\" cellpadding=\"0\"></table>", ownerDocument).addClass("tablesorter");
    $table.append($("<thead><tr><th>Members</th>" +
        "<th>Size</th><th>Density</th>" +
        "<th>In-weight</th><th>Out-weight</th>" +
        "<th>Quality</th></tr></thead>", ownerDocument));
    $tableBody = $("<tbody></tbody>", ownerDocument);
    $.each(this.clusters, function(index) {
      var $row = $("<tr></tr>", ownerDocument);
      var cluster = this;
      $row.append($("<td></td>", ownerDocument).text(cluster.members.join(", ")));
      $row.append($("<td></td>", ownerDocument).addClass("right").text(cluster.members.length));      
      $.each(numericFields, function() {
        var num = parseFloat(cluster[this]);
        if (isNaN(num))
          num = "";
        else
          num = num.toFixed(3);
        $row.append($("<td></td>", ownerDocument).addClass("right").text(num));
      });
      
      $tableBody.append($row);
    });
    $table.append($tableBody);
    
    sorting = sorting || [1, 1];
    
    $table.tablesorter({
      widgets: ['zebra'],
      headers: { 0: { sorter: false } },
      sortList: [sorting]
    });

    $target.append($table);
    $progress.remove();
    
    return $target;
  }
};

/** Creates a ClusterONEResult object from a JSON representation */
ClusterONEResult.fromJSON = function(json) {
  if (typeof(json) != 'object') {
    throw "TypeError";
  }
  
  var result = new ClusterONEResult();
  result.clusters = json.clusters;
  result.parameters = json.parameters;
  return result;
}
