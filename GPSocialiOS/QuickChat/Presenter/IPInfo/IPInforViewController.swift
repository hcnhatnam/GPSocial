//
//  IPInforViewController.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/21/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation
import UIKit
import GoogleMaps

class IPInforViewController: UIViewController {
  
  private var googleMapView: GMSMapView!
  private var userManager = LDUserManager()
  private var ip2LocationService = IP2LocationService()
  private let ownerMarker = GMSMarker()
  private var containerView = UIView()
  
  
  private var contryCodeLable = UILabel()
  private var ipv4Lable = UILabel()
  private var latitudeLable = UILabel()
  private var longitudeLable = UILabel()
  private var cityLable = UILabel()
  private var stateLable = UILabel()
  private var postalLable = UILabel()
  private var country_nameLable = UILabel()
  
  
  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = .white
    setupViews()
    
    userManager.getInfoOfUser(email: OwnerInfo.shared.email!) { (result) in
      switch result {
      case .success(let user):
        self.updateViewWithUser(user: user)
      case .failure(let error):
        print(error)
        self.showAlert(title: "Alert", message: "Failed to load info", completion: nil)
      }
    }
    
    setupOwnerMarker()
  }
  
  func setupOwnerMarker() {
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
  
  func updateViewWithUser(user: UserEntity) {
    ipv4Lable.text = "\(user.ipv4 ?? "-")"
    latitudeLable.text = "\(user.latitude ?? 0)"
    longitudeLable.text = "\(user.longitude ?? 0)"
    country_nameLable.text = user.country_name ?? "-"
    cityLable.text = user.city ?? "-"
  }
  
  override func loadView() {
    /// Setup camera
    let camera = GMSCameraPosition.camera(withLatitude: kCameraLatitude, longitude: kCameraLongitude, zoom: kDefaultCameraZoom)
    
    /// Set googleMapView as viewController.view
    googleMapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
    self.view = googleMapView
  }
  
  func setupViews() {
    containerView.backgroundColor = UIColor.black.withAlphaComponent(0.6)
    containerView.layer.cornerRadius = 16;
    view.addSubview(containerView)
    let paddingContainer: CGFloat = scaledValue(20)
    let containerHeight: CGFloat = scaledValue(200)
    containerView.snp.makeConstraints { (make) in
      make.left.equalTo(paddingContainer)
      make.top.equalTo(heightStatusBar() + paddingContainer)
      make.right.equalTo(-paddingContainer)
      make.height.equalTo(containerHeight)
    }
    
    
    let padding = scaledValue(10)
    let fontsize = scaledValue(20)
    
    /// iPv4:
    let leftIpv4 = UILabel()
    leftIpv4.text = "IPV4: "
    leftIpv4.textColor = .green
    leftIpv4.font = UIFont.systemFont(ofSize: fontsize, weight: .bold)
    leftIpv4.sizeToFit()
    containerView.addSubview(leftIpv4)
    leftIpv4.snp.makeConstraints { (make) in
      make.top.left.equalTo(padding)
      make.width.equalTo(leftIpv4.bounds.size.width)
    }
    
    containerView.addSubview(ipv4Lable)
    ipv4Lable.textColor = .white
    ipv4Lable.snp.makeConstraints { (make) in
      make.left.equalTo(leftIpv4.snp.right).offset(padding/2)
      make.right.equalTo(-padding)
      make.height.bottom.equalTo(leftIpv4)
    }
    
    /// latitude
    let leftLatitude = UILabel()
    leftLatitude.text = "Latitude: "
    leftLatitude.textColor = .green
    leftLatitude.font = UIFont.systemFont(ofSize: fontsize, weight: .bold)
    leftLatitude.sizeToFit()
    containerView.addSubview(leftLatitude)
    leftLatitude.snp.makeConstraints { (make) in
      make.left.equalTo(padding)
      make.top.equalTo(leftIpv4.snp.bottom).offset(padding)
      make.width.equalTo(leftLatitude.bounds.size.width)
    }
    
    containerView.addSubview(latitudeLable)
    latitudeLable.textColor = .white
    latitudeLable.snp.makeConstraints { (make) in
      make.left.equalTo(leftLatitude.snp.right).offset(padding/2)
      make.right.height.equalTo(ipv4Lable)
      make.bottom.equalTo(leftLatitude)
    }
    
    /// longtitude
    let leftLongitudo = UILabel()
    leftLongitudo.text = "Longitude: "
    leftLongitudo.textColor = .green
    leftLongitudo.font = UIFont.systemFont(ofSize: fontsize, weight: .bold)
    leftLongitudo.sizeToFit()
    containerView.addSubview(leftLongitudo)
    leftLongitudo.snp.makeConstraints { (make) in
      make.left.equalTo(padding)
      make.top.equalTo(leftLatitude.snp.bottom).offset(padding)
      make.width.equalTo(leftLongitudo.bounds.size.width)
    }
    
    containerView.addSubview(longitudeLable)
    longitudeLable.textColor = .white
    longitudeLable.snp.makeConstraints { (make) in
      make.left.equalTo(leftLongitudo.snp.right).offset(padding/2)
      make.right.height.equalTo(ipv4Lable)
      make.bottom.equalTo(leftLongitudo)
    }

    /// country name
    let leftCountryName = UILabel()
    leftCountryName.text = "Country Name: "
    leftCountryName.textColor = .green
    leftCountryName.font = UIFont.systemFont(ofSize: fontsize, weight: .bold)
    leftCountryName.sizeToFit()
    containerView.addSubview(leftCountryName)
    leftCountryName.snp.makeConstraints { (make) in
      make.left.equalTo(padding)
      make.top.equalTo(leftLongitudo.snp.bottom).offset(padding)
      make.width.equalTo(leftCountryName.bounds.size.width)
    }
    
    containerView.addSubview(country_nameLable)
    country_nameLable.textColor = .white
    country_nameLable.snp.makeConstraints { (make) in
      make.left.equalTo(leftCountryName.snp.right).offset(padding/2)
      make.right.height.equalTo(ipv4Lable)
      make.bottom.equalTo(leftCountryName)
    }
    
    /// city name
    let leftCityName = UILabel()
    leftCityName.text = "City Name: "
    leftCityName.textColor = .green
    leftCityName.font = UIFont.systemFont(ofSize: fontsize, weight: .bold)
    leftCityName.sizeToFit()
    containerView.addSubview(leftCityName)
    leftCityName.snp.makeConstraints { (make) in
      make.left.equalTo(padding)
      make.top.equalTo(country_nameLable.snp.bottom).offset(padding)
      make.width.equalTo(leftCityName.bounds.size.width)
    }
    
    containerView.addSubview(cityLable)
    cityLable.textColor = .white
    cityLable.snp.makeConstraints { (make) in
      make.left.equalTo(leftCityName.snp.right).offset(padding/2)
      make.right.height.equalTo(ipv4Lable)
      make.bottom.equalTo(leftCityName)
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
}
