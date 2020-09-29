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
  
  private var userOnlineCountLable = UILabel()
  private var googleMapView: GMSMapView!
  private var clusterManager: GMUClusterManager!
  private var ip2LocationService = IP2LocationService()
  
  private var conversations = [ObjectConversation]()
  private let manager = ConversationManager()
  private var currentUser: ObjectUser?
  private let userManager = UserManager()
  private let ownerMarker = GMSMarker()
  private let sv2UserManager = LDUserManager()
  private var users = [ObjectUser]()
  
  override func loadView() {
    /// Setup camera
    let camera = GMSCameraPosition.camera(withLatitude: kCameraLatitude, longitude: kCameraLongitude, zoom: kDefaultCameraZoom)
    
    /// Set googleMapView as viewController.view
    googleMapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
    self.view = googleMapView
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    addCountView()
    setupGoogleMap()
    fetchProfile()
    fetchConversations()
  }
  
  func addCountView() {
    let bg = UIView()
    bg.layer.cornerRadius = scaledValue(10)
    bg.frame = CGRect(x: scaledValue(16), y: scaledValue(60), width: scaledValue(190), height: scaledValue(50))
    bg.backgroundColor = UIColor.black.withAlphaComponent(0.8)
    bg.makeShadow()
    view.addSubview(bg)
    
    /// Count
    bg .addSubview(userOnlineCountLable)
    userOnlineCountLable.frame = CGRect(x: scaledValue(16), y: 0, width: 200, height: scaledValue(50))
    userOnlineCountLable.font = UIFont.systemFont(ofSize: 30, weight: .bold)
    userOnlineCountLable.textColor = .red
    
    let fixedLabel = UILabel()
    fixedLabel.text = "people are online"
    fixedLabel.frame = CGRect(x: scaledValue(45), y: 0, width: 200, height: scaledValue(50))
    fixedLabel.textColor = .white
    bg.addSubview(fixedLabel )
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    showNavigationBar(enableShow: false)
    fetchUsers()
  }
  
  func fetchUsers() {
    guard let id = userManager.currentUserID() else { return }
    userManager.contacts {[weak self] results in
      self?.users = results.filter({$0.id != id})
    }
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
    _markOwnerLocationOnMapView()
  }
    
  // MARK: Google Map Marker

  private func _markListUserOnMap(users: [UserEntity]) {
    print("Mark \(users.count) users on Map")
    for user in users {
      _markOnMapWithUser(user: user)
    }
  }
  
  func userEntityToObjectUser(userEntity: UserEntity) -> ObjectUser {
    let objUser = ObjectUser()
    
    objUser.id = userEntity.id ?? ""
    objUser.email = userEntity.email!
    objUser.profilePicLink = userEntity.profilePicLink ?? ""
    
    return objUser
  }
  
  private func _markOnMapWithUser(user: UserEntity) {
    if let position = user.position {
      let marker = LDMarker()
      marker.position = position
      marker.iconView = self.markerImageViewWithUrl(url: user.profilePicLink)
      marker.user = self.userEntityToObjectUser(userEntity: user)
      
      marker.map = googleMapView
      //clusterManager.add(marker)
    }
  }
  
  private func _markOwnerLocationOnMapView() {
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
  
  // MARK: Nam's server
  
  private func loginToNamServer(user: ObjectUser) {
    sv2UserManager.login(user: user) { (messageError) in
      if let messageError = messageError {
        self.showAlert(title: "Alert", message: messageError, completion: nil)
      } else {
        self.startTimerGetOnlineUsers()
      }
    }
  }
  
  private func startTimerGetOnlineUsers() {
    sv2UserManager.fetchOnlineusers { (result) in
      switch result {
      case .success(let users):
        self.googleMapView.clear()
        //self.clusterManager.clearItems()
        self._markListUserOnMap(users: users)
        self.userOnlineCountLable.text = "\(users.count)"
        //self.clusterManager.cluster()
      case .failure(let error):
        print(error)
      }
    }
  }

  // MARK: - GMUMapViewDelegate
  
  func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
    print("Did tap marker")
    mapView.animate(toLocation: marker.position)
    if let _ = marker.userData as? GMUCluster {
      mapView.animate(toZoom: mapView.camera.zoom + 1)
      NSLog("Did tap cluster")
      return true
    }
    
    if let marker = marker as? LDMarker {
      openMessageViewControllerWithFriend(friendUser: marker.user!)
    }
    
    return false
  }
  
  func openMessageViewControllerWithFriend(friendUser: ObjectUser) {
    guard let currentID = userManager.currentUserID() else { return }
    let vc: MessagesViewController = UIStoryboard.initial(storyboard: .messages)
    if let conversation = conversations.filter({$0.userIDs.contains(friendUser.id)}).first {
      vc.conversation = conversation
      vc.modalPresentationStyle = .pageSheet
      show(vc, sender: self)
      return
    }
    
    
    weak var weakS = self
    userManager.currentUserData { objectUser in
      guard let _  = objectUser,
            let weakSelf = weakS else { return }
      
      let conversation = ObjectConversation()
      // bỏ đoạn setid của Conversation thàn doleduy_nam
      //conversation.id = "\(ownerUser.name ?? "")_\(friendUser.name ?? "")"  /// ownerName_friendName
      conversation.userIDs.append(contentsOf: [currentID, friendUser.id])
      conversation.isRead = [currentID: true, friendUser.id: true]
      vc.conversation = conversation
      vc.modalPresentationStyle = .overFullScreen
      weakSelf.show(vc, sender: weakSelf)
    }
  }
  
  // MARK: - Private
  
  func _markRandom() {
    for position in self.genRandomPositions(count: kDebugTotalMarker) {
      _markOnMapViewWithPosition(position, .blue)
    }
  }
    
  func _markOnMapViewWithPosition(_ position: LDMapPosition, _ color: UIColor) {
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
 
  func markerImageViewWithUrl(url: String?) -> UIImageView {
    let imgView = UIImageView()
    if let url = url {
      imgView.setImage(url: URL(string: url))
    } else {
      imgView.image = UIImage(named: defaultAvatarName)
    }
    let avatarWitdh: CGFloat = 30
    imgView.frame = CGRect(x: 0, y: 0, width: avatarWitdh, height: avatarWitdh)
    imgView.layer.cornerRadius = avatarWitdh/2
    imgView.clipsToBounds = true
    imgView.layer.borderWidth = 0.5
    imgView.layer.borderColor = UIColor.systemBlue.cgColor
   
    return imgView
  }

}
