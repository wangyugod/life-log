class EventCtrl

  constructor: (@$log, @EventService,@$route) ->
    @$log.debug "constructing EventController"
    @events = []
    @tasks = []
    @userId = userId
    @getAllEvents(@userId)
    @getAllTasks(@userId)

  getAllEvents: (userId) ->
    @$log.debug "getAllEvents()"
    if(userId is "")
      @$log.debug "not logged in"
      return
    @EventService.listEvents(userId)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length} Events"
        @events = data
    ,
      (error) =>
        @$log.error "Unable to get Events: #{error}"
    )

  deleteEvent: (id) ->
      @$log.debug "remove event by id #{id}"
      @EventService.deleteEvent(id)
      .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Events"
          @$route.reload()
      ,
        (error) =>
          @$log.error "Unable to get Events: #{error}"
      )

  getAllTasks: (userId) ->
    @$log.debug "get all tasks"
    if(userId is "")
      @$log.debug "not logged in"
      return
    @EventService.listTasks(userId)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length} Tasks"
        @tasks = data
    ,
      (error) =>
        @$log.error "Unable to get Tasks: #{error}"
    )

  deleteTask: (id) ->
    @$log.debug "remove event by id #{id}"
    @EventService.deleteTask(id)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length} Events"
        @$route.reload()
    ,
      (error) =>
        @$log.error "Unable to get Events: #{error}"
    )

    finishTask: (id) ->
      @$log.debug "finish task id #{id}"
      @EventService.finishTask(id)
      .then(
        (data) =>
          @$route.reload()
      ,
        (error) =>
          @$log.error "Unable to finish task: #{error}"
      )

controllersModule.controller('EventCtrl', EventCtrl)