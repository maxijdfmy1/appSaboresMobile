package com.example.saboresdehogar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saboresdehogar.R
import com.example.saboresdehogar.model.user.UserRole
import com.example.saboresdehogar.screens.*
import com.example.saboresdehogar.viewmodel.AuthState
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
import com.example.saboresdehogar.viewmodel.OrderViewModel
import com.example.saboresdehogar.viewmodel.UserViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object About : Screen("about")
    object MyOrders : Screen("my_orders")
    object Catalog : Screen("catalog")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout/{total}") {
        fun createRoute(total: Int) = "checkout/$total"
    }
    object CheckoutSuccess : Screen("checkout_success/{orderId}") {
        fun createRoute(orderId: String) = "checkout_success/$orderId"
    }
    object CheckoutFail : Screen("checkout_fail")
    object AdminProductList : Screen("admin_product_list")
    object AdminAddProduct : Screen("admin_add_product")
    object AdminMenu : Screen("admin_menu")
    object AdminEditProduct : Screen("admin_edit_product/{productId}") {
        fun createRoute(productId: String) = "admin_edit_product/$productId"
    }
    object AdminOrders : Screen("admin_orders")
    object AdminUserList : Screen("admin_user_list")
    object ProfileEdit : Screen("profile_edit")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaboresApp() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authState by authViewModel.authState.observeAsState()
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != Screen.Splash.route &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route &&
                currentRoute?.startsWith("admin") == false
            ) {
                SaboresTopAppBar(
                    currentRoute = currentRoute,
                    navController = navController,
                    authViewModel = authViewModel,
                    onBackClick = { 
                        if (currentRoute == Screen.Catalog.route) {
                            navController.navigate(Screen.Home.route)
                        } else {
                            navController.navigateUp()
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Catalog.route || currentRoute?.startsWith("product_detail") == true) {
                val cartViewModel: CartViewModel = viewModel(factory = factory)
                val itemCount by cartViewModel.itemCount.observeAsState(0)
                CartFab(itemCount = itemCount, onClick = { navController.navigate(Screen.Cart.route) })
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            authViewModel = authViewModel,
            factory = factory
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    factory: ViewModelFactory
) {
    val authState by authViewModel.authState.observeAsState()
    val currentUser by authViewModel.currentUser.observeAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            LaunchedEffect(authState, currentUser) {
                delay(1000)
                val route = when (authState) {
                    is AuthState.Authenticated -> {
                        if (currentUser?.role == UserRole.ADMIN) Screen.AdminProductList.route else Screen.Home.route
                    }
                    is AuthState.Unauthenticated -> Screen.Login.route
                    else -> null
                }
                if (route != null) {
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) }
        composable(Screen.Register.route) { RegisterScreen(navController, authViewModel) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.About.route) { AboutUsScreen() }
        composable(Screen.MyOrders.route) {
            val orderViewModel: OrderViewModel = viewModel(factory = factory)
            MyOrdersScreen(navController, orderViewModel)
        }
        composable(Screen.Catalog.route) {
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            CatalogScreen(navController, menuViewModel, cartViewModel)
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            ProductDetailScreen(navController, productId, menuViewModel, cartViewModel)
        }
        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            val orderViewModel: OrderViewModel = viewModel(factory = factory)
            CartScreen(navController, authViewModel, cartViewModel, orderViewModel)
        }
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("total") { type = NavType.IntType })
        ) { backStackEntry ->
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            CheckoutScreen(navController, total)
        }
        composable(
            route = Screen.CheckoutSuccess.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val orderViewModel: OrderViewModel = viewModel(factory = factory)
            if (orderId != null) {
                CheckoutSuccessScreen(navController, orderId, orderViewModel)
            } else {
                CheckoutFailScreen(navController)
            }
        }
        composable(Screen.CheckoutFail.route) { CheckoutFailScreen(navController) }

        // Admin routes
        composable(Screen.AdminProductList.route) {
            AdminProductListScreen(navController, authViewModel)
        }
        composable(Screen.AdminAddProduct.route) {
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            AdminAddProductScreen(navController, menuViewModel) // Updated
        }
        composable(Screen.AdminMenu.route) {
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            AdminMenuScreen(navController, menuViewModel)
        }
        composable(
             route = Screen.AdminEditProduct.route,
             arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
             val productId = backStackEntry.arguments?.getString("productId") ?: ""
             val menuViewModel: MenuViewModel = viewModel(factory = factory)
             AdminEditProductScreen(navController, menuViewModel, productId)
        }
        composable(Screen.AdminOrders.route) {
            val orderViewModel: OrderViewModel = viewModel(factory = factory)
            AdminOrderListScreen(navController, orderViewModel)
        }
        composable(Screen.AdminUserList.route) {
             val userViewModel: UserViewModel = viewModel(factory = factory)
             AdminUserListScreen(navController, userViewModel)
        }
        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaboresTopAppBar(
    currentRoute: String?,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val title = when (currentRoute) {
        Screen.Home.route -> ""
        Screen.Catalog.route -> "Menú"
        Screen.Cart.route -> "Tu Carrito"
        Screen.Checkout.route -> "Pagar"
        Screen.About.route -> "Sobre Nosotros"
        Screen.MyOrders.route -> "Mis Pedidos"
        else -> ""
    }

    val showBackButton = currentRoute != Screen.Home.route
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (title.isNotEmpty()) Text(title, fontWeight = FontWeight.Bold)
            else Image(
                painter = painterResource(id = R.drawable.sabores),
                contentDescription = "Logo Sabores de Hogar",
                modifier = Modifier.height(40.dp)
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.AccountCircle, "Perfil")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar perfil") },
                        onClick = {
                            menuExpanded = false
                            navController.navigate(Screen.ProfileEdit.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar sesión") },
                        onClick = {
                            menuExpanded = false
                            authViewModel.logout()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun CartFab(itemCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    ) {
        BadgedBox(badge = { if (itemCount > 0) Badge { Text("$itemCount") } }) {
            Icon(Icons.Default.ShoppingCart, "Ver Carrito")
        }
    }
}

@Composable
fun PlaceholderScreen(text: String, onNavigate: (() -> Unit)? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = MaterialTheme.typography.headlineSmall)
            if (onNavigate != null) {
                Spacer(Modifier.height(20.dp))
                Button(onClick = onNavigate) { Text("Navegar (Test)") }
            }
        }
    }
}
