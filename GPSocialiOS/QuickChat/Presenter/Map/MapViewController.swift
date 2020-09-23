//
//  MapViewController.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/19/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import GoogleMaps
import UIKit
import GoogleMapsUtils
import AlamofireImage
import Alamofire

typealias LDMapPosition = CLLocationCoordinate2D

let kDebugTotalMarker = 100
let kCameraLatitude: CLLocationDegrees = 16.0
let kCameraLongitude: CLLocationDegrees = 106.0
let kDefaultCameraZoom: Float = 4.0

class MapViewController: UIViewController, GMSMapViewDelegate {
  
  private var googleMapView: GMSMapView!
  private var clusterManager: GMUClusterManager!
  private var ip2LocationService = IP2LocationService()
  
  private var conversations = [ObjectConversation]()
  private let manager = ConversationManager()
  private var currentUser: ObjectUser?
  private let userManager = UserManager()
  private let ownerMarker = GMSMarker()
  private let sv2UserManager = LDUserManager()
  
  override func loadView() {
    /// Setup camera
    let camera = GMSCameraPosition.camera(withLatitude: kCameraLatitude, longitude: kCameraLongitude, zoom: kDefaultCameraZoom)
    
    /// Set googleMapView as viewController.view
    googleMapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
    self.view = googleMapView
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    setupGoogleMap()
    fetchProfile()
    fetchConversations()
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    showNavigationBar(enableShow: true)
  }
    
  func setupGoogleMap() {
    /// Setup ClusterManager
    let iconGenerator = GMUDefaultClusterIconGenerator()
    let algorithm = GMUNonHierarchicalDistanceBasedAlgorithm()
    let renderer = GMUDefaultClusterRenderer(mapView: googleMapView, clusterIconGenerator: iconGenerator)
    clusterManager = GMUClusterManager(map: googleMapView, algorithm: algorithm, renderer: renderer)
    clusterManager.setMapDelegate(self)
    
    /// Mark owner location on GoogleMapView
    /// markRandom()
    
    /// Setup ownermarker
    ownerMarker.icon = GMSMarker.markerImage(with: .red)
    ownerMarker.map = googleMapView
    
    let ownerAvatart = UIImageView(image: UIImage(named: defaultAvatarName))
    let avatarWitdh: CGFloat = 30
    ownerAvatart.frame = CGRect(x: 0, y: 0, width: avatarWitdh, height: avatarWitdh)
    ownerAvatart.layer.cornerRadius = avatarWitdh/2
    ownerAvatart.clipsToBounds = true
    ownerAvatart.layer.borderWidth = 1.0
    ownerAvatart.layer.borderColor = UIColor.black.cgColor

    ownerMarker.iconView = ownerAvatart
    ownerMarker.tracksViewChanges = true
    //markOwnerLocationOnMapView()
  }
  
  func markerImageViewWithUrl(url: String?) -> UIImageView {
    let imgView = UIImageView()
    if let url = url {
      imgView.setImage(url: URL(string: url))
    } else {
      imgView.image = UIImage(named: "defaultAvatarName")
    }
    let avatarWitdh: CGFloat = 30
    imgView.frame = CGRect(x: 0, y: 0, width: avatarWitdh, height: avatarWitdh)
    imgView.layer.cornerRadius = avatarWitdh/2
    imgView.clipsToBounds = true
    imgView.layer.borderWidth = 1.0
    imgView.layer.borderColor = UIColor.black.cgColor
   
    return imgView
  }
  
  func startTimerGetOnlineUsers() {
    sv2UserManager.startPingToServer { (result) in
      switch result {
      case .success(let users):
        self.googleMapView.clear()
        self.markListUserOnMap(users: users)
      case .failure(let error):
        print(error)
      }
    }
  }
  
  func markListUserOnMap(users: [UserEntity]) {
    print("Mark \(users.count) users on Map")
    for user in users {
      markOnMapWithUser(user: user)
    }
  }
  
  func markOnMapWithUser(user: UserEntity) {
    if let position = user.position {
      let marker = GMSMarker()
      marker.position = position
      marker.iconView = self.markerImageViewWithUrl(url: user.profilePicLink)
      
      marker.map = googleMapView
    }
  }
  
  func loginToNamServer(user: ObjectUser) {
    sv2UserManager.login(user: user) { (messageError) in
      if let messageError = messageError {
        self.showAlert(title: "Alert", message: messageError, completion: nil)
      } else {
        self.startTimerGetOnlineUsers()
      }
    }
  }
  
  // MARK: - GMUMapViewDelegate
  
  func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
    mapView.animate(toLocation: marker.position)
    if let _ = marker.userData as? GMUCluster {
      mapView.animate(toZoom: mapView.camera.zoom + 1)
      NSLog("Did tap cluster")
      return true
    }
    
    let vc: MessagesViewController = UIStoryboard.initial(storyboard: .messages)
    vc.conversation = conversations[0]
    vc.modalPresentationStyle = .fullScreen
    manager.markAsRead(conversations[0])
    show(vc, sender: self)
    
    NSLog("Did tap marker")
    return false
  }
  
  // MARK: - Private
  
  func markRandom() {
    for position in self.genRandomPositions(count: kDebugTotalMarker) {
      markOnMapViewWithPosition(position, .blue)
    }
  }
  
  func markOwnerLocationOnMapView() {
    ip2LocationService.loadOwnerIP { result in
      switch result {
      case .success(let ownerIP):
        self.ip2LocationService.getLocationOfIP(ip: ownerIP) { (result) in
          switch result {
          case .success(let position):
            self.ownerMarker.position = position
          case .failure(let error):
            self.showAlert(title: "Alert", message: error.localizedDescription, completion: nil)
          }
        }
      case .failure(let error):
        self.showAlert(title: "Alert", message: error.localizedDescription, completion: nil)
      }
    }
  }
  
  func markOnMapViewWithPosition(_ position: LDMapPosition, _ color: UIColor) {
    let marker = self.createMarkerFromPosition(position, color)
    marker.map = self.googleMapView
  }
}

// MARK: - Conversations

extension MapViewController {
  
  func fetchConversations() {
    manager.currentConversations {[weak self] conversations in
      self?.conversations = conversations.sorted(by: {$0.timestamp > $1.timestamp})
    }
  }
  
  func fetchProfile() {
    userManager.currentUserData {[weak self] user in
      if let user = user {
        self?.currentUser = user
        
        /// Update ownerMarker after fetch done currentUser
        if let urlString = user.profilePicLink {
          self?.updateOwnerMarkerIconWithUrl(url: urlString)
        }
        
        self?.loginToNamServer(user: user)
      }
    }
  }
  
  func updateOwnerMarkerIconWithUrl(url: String) {
    (self.ownerMarker.iconView as! UIImageView).setImage(url: URL(string: url))
  }

}

// MARK: - Helper

extension MapViewController {
  
  func createMarkerFromPosition(_ position: LDMapPosition, _ color: UIColor) -> GMSMarker {
    let marker = GMSMarker(position: position)
    marker.icon = GMSMarker.markerImage(with: color)
    //marker.title = "This is title"
    
    return marker
  }
  
  func genRandomPositions(count: Int) -> [LDMapPosition] {
    var listPosition: [LDMapPosition] = []
    for _ in 1...count {
      listPosition.append(LDMapPosition(latitude: self.randomLatitude(), longitude: self.randomLongitude()))
    }
    return listPosition
  }
  
  func randomLatitude() -> CLLocationDegrees {
    return CLLocationDegrees(Int.random(in: -90...90))
  }
  
  func randomLongitude() -> CLLocationDegrees {
    return CLLocationDegrees(Int.random(in: -180...180))
  }
  
}
