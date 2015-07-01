
dependencies = [
    'ngRoute',
    'ui.bootstrap',
    'myApp.filters',
    'myApp.services',
    'myApp.controllers',
    'myApp.directives',
    'myApp.common',
    'myApp.routeConfig'
]

app = angular.module('myApp', dependencies)

angular.module('myApp.routeConfig', ['ngRoute'])
    .config ($routeProvider) ->
        $routeProvider
            .when('/', {
                templateUrl: '/assets/partials/view.html'
            })
            .when('/signup', {
                templateUrl: '/assets/partials/users/create.html'
            })
            .when('/users/create', {
                templateUrl: '/assets/partials/users/create.html'
            })
            .when('/users/edit/:firstName/:lastName', {
                templateUrl: '/assets/partials/update.html'
            })
            .when('/event/create', {
                templateUrl: '/assets/partials/event/create.html'
            })
            .when('/task/create', {
                templateUrl: '/assets/partials/event/createTask.html'
            })
    .config ($locationProvider) ->
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        })

@commonModule = angular.module('myApp.common', [])
@controllersModule = angular.module('myApp.controllers', [])
@servicesModule = angular.module('myApp.services', [])
@modelsModule = angular.module('myApp.models', [])
@directivesModule = angular.module('myApp.directives', [])
@filtersModule = angular.module('myApp.filters', [])