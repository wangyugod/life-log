class EventService

  @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
  @defaultConfig = { headers: @headers }

  constructor: (@$log, @$http, @$q) ->
    @$log.debug "constructing EventService"

  createEvent: (event) ->
    @$log.debug "create event  #{angular.toJson(event, true)}"
    deferred = @$q.defer()
    @$http.post('/event', event)
    .success((data, status, headers) =>
      @$log.info("Successfully created event - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to create event - status #{status}")
      deferred.reject(data)
    )
    deferred.promise

  listEvents: (userId) ->
    @$log.debug "find events by userId #{userId}"
    deferred = @$q.defer()

    @$http.get("/events/" + userId)
    .success((data, status, headers) =>
      @$log.info("Successfully listed Users - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to list Users - status #{status}")
      deferred.reject(data)
    )
    deferred.promise

  deleteEvent: (id) ->
    @$log.debug "delete event by id #{id}"
    deferred = @$q.defer()

    @$http.delete("/event/" + id)
    .success((data, status, headers) =>
      @$log.info "successfully delete event - status #{status}"
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error "Failed to delete event - status #{status}"
      deferred.reject(data)
    )
    deferred.promise

  createTask: (task) ->
    @$log.debug "create task  #{angular.toJson(task, true)}"
    deferred = @$q.defer()
    @$http.post('/task', task)
    .success((data, status, headers) =>
      @$log.info("Successfully created task - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to create task - status #{status}")
      deferred.reject(data)
    )
    deferred.promise

  finishTask: (taskId) ->
    @$log.debug "finish task"
    deferred = @$q.defer()
    @$http.post('/task/finish/' + taskId)
    .success((data, status, headers) =>
      @$log.info "successfully finish task - status #{status}"
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to finish task - status #{status}")
      deferred.reject(data)
    )
    deferred.promise

  listTasks: (userId) ->
    @$log.debug "find events by userId #{userId}"
    deferred = @$q.defer()

    @$http.get("/tasks/" + userId)
    .success((data, status, headers) =>
      @$log.info("Successfully listed Users - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to list Users - status #{status}")
      deferred.reject(data)
    )
    deferred.promise

  deleteTask: (id) ->
    @$log.debug "delete task by id #{id}"
    deferred = @$q.defer()

    @$http.delete("/task/" + id)
    .success((data, status, headers) =>
      @$log.info "successfully delete task - status #{status}"
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error "Failed to delete task - status #{status}"
      deferred.reject(data)
    )
    deferred.promise
servicesModule.service('EventService', EventService)