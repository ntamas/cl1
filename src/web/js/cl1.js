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
  
  this.debugMode = true;
  
  this.init();
}

ClusterONEFrontend.prototype = {
  /** Adds a progress marker to the current step next to the buttons */
  addProgressMarker: function(message, step) {
    if (step == null)
      step = this.currentStep;

    var $item = $("#steps li").slice(step-1, step);
    
    if ($item.length > 0) {
      var $marker = $(".progress-indicator", $item);
      if ($marker.length == 0) {
        $marker = $("<span class=\"progress-indicator\"></span>");
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
  
  /** Clears all the error messages */
  clearErrors: function() {
    return this.clearGenericMessage("errors");
  },
  
  /** Clears a generic message container */
  clearGenericMessage: function(container_id) {
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
      url: url,
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
    $("#algorithm_parameters").validate({
      submitHandler: function(form) {
        alert("submitted");
      }
    });
    
    /* Set the active step */
    this.setActiveStep(1);
  },
  
  /** Event handler invoked when an error happened during an AJAX call */
  onAJAXError: function(req, status, exc) {
    this.showBug("AJAX call returned with HTTP error code "+req.status+".");
    if (req)
      this.debug($.httpData(req));
    this.removeProgressMarkers();
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
    if (response.substr(0, 7) == "http://" ||
        response.substr(0, 8) == "https://") {
      /* Successful file upload, response contains the new URL */
      this.datasetUrl = response;
      this.setActiveStep(2);
    } else {
      /* Error while uploading file */
      this.showError("Error while uploading file. Please try again later.");
    }
  },
  
  /** Removes a progress marker from the current or given step */
  removeProgressMarker: function(step) {
    if (step == null)
      step = this.currentStep;
      
    var $item = $("#steps li").slice(step-1, step);
    if ($item.length > 0)
      $(".progress-indicator", $item).remove();
  },
  
  /** Removes all the progress markers from the page */
  removeProgressMarkers: function(step) {
    $("#steps li .progress-indicator").remove();
  },
  
  /** Retrieves the results from the stored result URL */
  retrieveResults: function() {
    var settings = this.getDefaultAjaxOptions(this.resultUrl);
    settings.success = function(data, status, req) {
      this.removeProgressMarker(2);
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
    
    this.removeProgressMarkers();
    
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
      this.showError("Some algorithm parameters are invalid, please fix them first!");
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
    
    $.each($("#algorithm_parameters").serializeArray(), function(index, obj) {
      data[obj.key] = obj.value;
    });
    
    alert(data);
     
    this.addProgressMarker("Please wait, running calculations...", 2);
    $.ajax(settings);
  } 
};

/***************************************************************************/

/** Class representing the results of a Cluster ONE run */
function ClusterONEResult() {
  this.clusters = [];
}

ClusterONEResult.prototype = {
  /** Downloads the results by opening a popup window and rendering the
   * results in there in a simple plain text format
   */
  download: function() {
    var win = this.getPopup("download-window");
    if (!win)
      return;
    
    doc = win.document;
    if (doc.open)
      doc.open("text/plain");

    $.each(this.clusters, function(index) {
      doc.write(this.members.join(" "));
      doc.write("\n");
    });
    
    if (doc.close)
      doc.close();
  },
  
  getPopup: function(popupId) {
    var win = window.open("", popupId);
    if (!win) {
      alert("Could not open popup window. Please disable your popup blocker!");
    }
    return win;
  },
  
  /** Prints the results by opening a popup window, rendering the results
   * in a div in the popup and asking the browser to print the popup.
   */
  print: function() {
    var win = this.getPopup("print-window");
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
  render: function(id) {
    var $target, $properties, $item;
    
    $target = $(id);
    $target.empty();
    
    $properties = $("<dl></dl>").addClass("compact");
    $item = $("<dt></dt>").text("Number of clusters:");
    $properties.append($item);
    $item = $("<dd></dd>").text(this.clusters.length);
    $properties.append($item);    
    $target.append($properties);
    
    $table = $("<table cellspacing=\"0\" cellpadding=\"0\"></table>");
    $table.append($("<thead><tr><th class=\"right\">#</th><th>Members</th></tr></thead>"));
    $tableBody = $("<tbody></tbody>");
    $.each(this.clusters, function(index) {
      var $row = $("<tr></tr>");
      $row.append($("<td>" + (index+1) + ".</td>").addClass("right"));
      $row.append($("<td></td>").text(this.members.join(", ")));
      $tableBody.append($row);
    });
    $table.append($tableBody);
    $target.append($table);
    
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
  return result;
}
