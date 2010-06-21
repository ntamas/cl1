function ClusterONEFrontend() {
  this.datasetUrl = null;
  this.resultUrl = null;
  
  this.debugMode = true;
  this.init();
}

/** Initializes the Cluster ONE frontend page */
ClusterONEFrontend.prototype = {
  debug: function(msg) {
    if (!this.debugMode)
      return;
    $('#debug').html('<pre>'+msg+'</pre>');
  },
  
  init: function() {
    var frontend = this;
    
    /* Set up the upload button */
    new AjaxUpload('upload-button', {
      action: 'api/dataset',
      name: 'file',
      autoSubmit: true,
      responseType: false,
      onComplete: function(file, response) {
        frontend.onFileUploaded.call(frontend, file, response);
      }
    });
    
    /* Set up the start button */
    $('#start-button').click(function() { frontend.startAnalysis.call(frontend); });
    
    /* Set the active step */
    this.setActiveStep(1);
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
    
    this.setActiveStep(3);
  },
  
  /** Event handler invoked when the analysis results have arrived */
  onAnalysisError: function(req, status, exc) {
    this.showBug("AJAX call returned with HTTP error code "+req.status+".");
    if (req)
      this.debug($.httpData(req));
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
  
  /** Sets the active step in the frontend */
  setActiveStep: function(activeStep) {
    $("#steps li").each(function(index) {
      index = index + 1;
      $(this).toggleClass("finished", (activeStep > index));
      $(this).toggleClass("active", (activeStep == index));
      
      if (activeStep < index) {
        $(this, 'button').attr("disabled", "disabled");
      } else {
        $(this, 'button').removeAttr("disabled");
      }
    });
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
    
    var container = $("#"+container_id);
    container.empty();
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
    
    var slashPos = this.datasetUrl.lastIndexOf("/");
    if (slashPos < 0) {
      this.showBug("Invalid dataset URL.");
      return;
    }
    
    var datasetId = this.datasetUrl.substr(slashPos+1);
    var data = { dataset_id: datasetId };
    var settings = {
      context: this,
      data: data,
      error: this.onAnalysisError,
      type: 'POST',
      success: this.onAnalysisCompleted,
      url: 'api/result'
    };
    $.ajax(settings);
  }
};
