class CreateTaskCtrl

  constructor: (@$log, @$location, @EventService) ->
    @$log.debug "constructing CreateEventController"
    @task = {}

  createTask: () ->
    @$log.debug "createTask() with userId #{userId} and #{angular.toJson(@task, true)}"
    if(userId is "")
      @$log.debug "not logged in"
      return
    @task.userId = userId
    @EventService.createTask(@task)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data} task"
        @task = data
        @$location.path("/")
    ,
      (error) =>
        @$log.error "Unable to create Task: #{error}"
        @task.error = error
    )

controllersModule.controller('CreateTaskCtrl', CreateTaskCtrl)